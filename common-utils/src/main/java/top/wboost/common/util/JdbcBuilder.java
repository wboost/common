package top.wboost.common.util;

import lombok.Data;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JdbcBuilder {

    public static JdbcExecutor createExecutors(String USERNAME, String PASSWORD, String DRIVER, String URL) {
        return new JdbcExecutor(USERNAME, PASSWORD, DRIVER, URL);
    }

    public static JdbcExecutor createExecutors(DataSource dataSource) {
        return new JdbcExecutor(dataSource);
    }

    @Data
    public static class JdbcExecutor {

        //数据库用户名
        private final String USERNAME;
        //数据库密码
        private final String PASSWORD;
        //驱动信息
        private final String DRIVER;
        //数据库地址
        private final String URL;
        //连接池
        private final DataSource dataSource;

        public JdbcExecutor(String USERNAME, String PASSWORD, String DRIVER, String URL) {
            this(USERNAME,PASSWORD,DRIVER,URL,null);
        }

        public JdbcExecutor(DataSource dataSource) {
            this(null,null,null,null,dataSource);
        }

        private JdbcExecutor(String USERNAME, String PASSWORD, String DRIVER, String URL,DataSource dataSource) {
            this.dataSource = dataSource;
            this.USERNAME = USERNAME;
            this.PASSWORD = PASSWORD;
            this.DRIVER = DRIVER;
            this.URL = URL;
        }

        /**
         * 获得数据库的连接
         *
         * @return
         */
        public Connection getConnection() {
            try {
                if (this.dataSource != null) {
                    return dataSource.getConnection();
                } else {
                    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void closeConnection(Connection connection) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ioe) {
                // ignore
            }
        }

        /**
         * 增加、删除、改
         *
         * @param sql
         * @return
         * @throws SQLException
         */
        public boolean updateByPreparedStatement(String sql) throws SQLException {
            boolean flag = false;
            int result = -1;
            Connection connection = getConnection();
            Statement pstmt = connection.createStatement();
            int index = 1;
            result = pstmt.executeUpdate(sql);
            flag = result > 0 ? true : false;
            closeConnection(connection);
            return flag;
        }

        /**
         * 查询单条记录
         *
         * @param sql
         * @return
         * @throws SQLException
         */
        public Map<String, Object> findSimpleResult(String sql) throws SQLException {
            Map<String, Object> map = new LinkedHashMap<>();
            int index = 1;
            Connection connection = getConnection();
            Statement pstmt = connection.createStatement();
            ResultSet resultSet = pstmt.executeQuery(sql);//返回查询结果
            ResultSetMetaData metaData = resultSet.getMetaData();
            int col_len = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 0; i < col_len; i++) {
                    String cols_name = metaData.getColumnName(i + 1);
                    Object cols_value = resultSet.getObject(cols_name);
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    map.put(cols_name, cols_value);
                }
            }
            closeConnection(connection);
            return map;
        }

        /**
         * 查询多条记录
         *
         * @param sql
         * @return
         * @throws SQLException
         */
        public List<Map<String, Object>> findModeResult(String sql) throws SQLException {
            Connection connection = getConnection();
            Statement pstmt = connection.createStatement();
            ResultSet resultSet = pstmt.executeQuery(sql);
            List<Map<String, Object>> result = resolveResultSet(resultSet);
            closeConnection(connection);
            return result;
        }

        public List<Map<String, Object>> resolveResultSet(ResultSet resultSet) throws SQLException {
            List<Map<String, Object>> list = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols_len = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < cols_len; i++) {
                    String cols_name = metaData.getColumnName(i + 1);
                    Object cols_value = resultSet.getObject(cols_name);
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    map.put(cols_name, cols_value);
                }
                list.add(map);
            }

            return list;
        }

        /**
         * 通过反射机制查询单条记录
         *
         * @param sql
         * @param cls
         * @return
         * @throws Exception
         */
        public <T> T findSimpleRefResult(String sql, Class<T> cls) throws Exception {
            T resultObject = null;
            int index = 1;
            Connection connection = getConnection();
            Statement pstmt = connection.createStatement();
            ResultSet resultSet = pstmt.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols_len = metaData.getColumnCount();
            while (resultSet.next()) {
                //通过反射机制创建一个实例
                resultObject = cls.newInstance();
                for (int i = 0; i < cols_len; i++) {
                    String cols_name = metaData.getColumnName(i + 1);
                    Object cols_value = resultSet.getObject(cols_name);
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    Field field = cls.getDeclaredField(cols_name);
                    field.setAccessible(true); //打开javabean的访问权限
                    field.set(resultObject, cols_value);
                }
            }
            closeConnection(connection);
            return resultObject;

        }

        /**
         * 通过反射机制查询多条记录
         *
         * @param sql
         * @param cls
         * @return
         * @throws Exception
         */
        public <T> List<T> findMoreRefResult(String sql, Class<T> cls) throws Exception {
            List<T> list = new ArrayList<T>();
            int index = 1;
            Connection connection = getConnection();
            Statement pstmt = connection.createStatement();

            ResultSet resultSet = pstmt.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int cols_len = metaData.getColumnCount();
            while (resultSet.next()) {
                //通过反射机制创建一个实例
                T resultObject = cls.newInstance();
                for (int i = 0; i < cols_len; i++) {
                    String cols_name = metaData.getColumnName(i + 1);
                    Object cols_value = resultSet.getObject(cols_name);
                    if (cols_value == null) {
                        cols_value = "";
                    }
                    Field field = cls.getDeclaredField(cols_name);
                    field.setAccessible(true); //打开javabean的访问权限
                    field.set(resultObject, cols_value);
                }
                list.add(resultObject);
            }
            closeConnection(connection);
            return list;
        }
    }

}
