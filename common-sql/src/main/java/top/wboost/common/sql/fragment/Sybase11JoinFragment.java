package top.wboost.common.sql.fragment;

public class Sybase11JoinFragment extends JoinFragment {

    private StringBuilder afterFrom = new StringBuilder();
    private StringBuilder afterWhere = new StringBuilder();

    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType) {

        addCrossJoin(tableName, alias);

        for (int j = 0; j < fkColumns.length; j++) {
            //full joins are not supported.. yet!
            if (joinType == JoinType.FULL_JOIN) {
                throw new UnsupportedOperationException();
            }

            afterWhere.append(" and ").append(fkColumns[j]).append(" ");

            if (joinType == JoinType.LEFT_OUTER_JOIN) {
                afterWhere.append('*');
            }
            afterWhere.append('=');
            if (joinType == JoinType.RIGHT_OUTER_JOIN) {
                afterWhere.append("*");
            }

            afterWhere.append(" ").append(alias).append('.').append(pkColumns[j]);
        }
    }

    public String toFromFragmentString() {
        return afterFrom.toString();
    }

    public String toWhereFragmentString() {
        return afterWhere.toString();
    }

    public void addJoins(String fromFragment, String whereFragment) {
        afterFrom.append(fromFragment);
        afterWhere.append(whereFragment);
    }

    public JoinFragment copy() {
        Sybase11JoinFragment copy = new Sybase11JoinFragment();
        copy.afterFrom = new StringBuilder(afterFrom.toString());
        copy.afterWhere = new StringBuilder(afterWhere.toString());
        return copy;
    }

    public void addCondition(String alias, String[] columns, String condition) {
        for (String column : columns) {
            afterWhere.append(" and ").append(alias).append('.').append(column).append(condition);
        }
    }

    public void addCrossJoin(String tableName, String alias) {
        afterFrom.append(", ").append(tableName).append(' ').append(alias);
    }

    public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
        throw new UnsupportedOperationException();

    }

    public boolean addCondition(String condition) {
        return addCondition(afterWhere, condition);
    }

    public void addFromFragmentString(String fromFragmentString) {
        afterFrom.append(fromFragmentString);
    }

    public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, JoinType joinType,
            String on) {
        addJoin(tableName, alias, fkColumns, pkColumns, joinType);
        addCondition(on);
    }
}
