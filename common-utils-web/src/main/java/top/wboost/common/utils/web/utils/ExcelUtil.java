package top.wboost.common.utils.web.utils;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wanglf
 * @date 2018/11/7 17:42
 */
public class ExcelUtil {
    private static final String EMPTY = "";
    private static final String SHEET_NAME = "Sheet1";
    private static final int DEFAULT_BEGIN_ROW = -1;

    /**
     * 外部导入Excel标准目录数据
     * @param file 导入文件
     * @return List<Map               <               String               ,                               String>>
     */
    public static List<Map<String, String>> importExcel(MultipartFile file) throws Exception {
        return readExcel(file.getInputStream());
    }

    /**
     * 读取excel到一个sheet
     * @param fileStream excel文件流
     */
    public static List<Map<String, String>> readExcel(InputStream fileStream) throws Exception {
        return readExcel(fileStream, DEFAULT_BEGIN_ROW);
    }

    /**
     * 读取excel到一个sheet
     * @param fileStream excel文件流
     * @param beginRow 开始读取行数
     */
    public static List<Map<String, String>> readExcel(InputStream fileStream, Integer beginRow) throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(fileStream);
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (beginRow == DEFAULT_BEGIN_ROW)
                beginRow = sheet.getFirstRowNum();

            if (beginRow == 0 && sheet.getLastRowNum() == 0) {
                continue;
            }
            list.addAll(readExcel(beginRow, sheet.getLastRowNum(), sheet));
        }
        return list;
    }

    /**
     * 读取excel到多个sheet
     * @param fileStream excel文件流
     */
    public static Map<String, List<Map<String, String>>> readExcelFromSheets(InputStream fileStream) throws Exception {
        return readExcelFromSheets(fileStream, DEFAULT_BEGIN_ROW);
    }

    /**
     * 读取excel到多个sheet
     * @param fileStream excel文件流
     * @param beginRow 开始读取行数
     * @return 返回各个sheet的数据
     * @throws Exception
     */
    public static Map<String, List<Map<String, String>>> readExcelFromSheets(InputStream fileStream, Integer beginRow) throws Exception {
        Workbook workbook = WorkbookFactory.create(fileStream);
        Map<String, List<Map<String, String>>> sheetsMap = new HashMap<>();
        int sheetNum = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetNum; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (beginRow == DEFAULT_BEGIN_ROW)
                beginRow = sheet.getFirstRowNum();

            if (beginRow == 0 && sheet.getLastRowNum() == 0) {
                continue;
            }
            sheetsMap.put(sheet.getSheetName(), readExcel(beginRow, sheet.getLastRowNum(), sheet));
        }
        return sheetsMap;
    }

    /**
     * 读取excel
     * @param beginRow 首行
     * @param lastRow 末行
     * @param sheet sheet
     * @return List<Map               <               String               ,                               String>>
     */
    private static List<Map<String, String>> readExcel(int beginRow, int lastRow, Sheet sheet) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int j = beginRow; j <= lastRow; j++) {
            Map<String, String> map = new TreeMap<>();
            Row row = sheet.getRow(j);
            if (null == row) {
                continue;
            }
            list.add(map);
            for (int k = row.getFirstCellNum(); k < row.getLastCellNum(); k++) {
                Cell cell = row.getCell(k);
                if (cell == null)
                    map.put(String.valueOf(k), EMPTY);
                else {
                    map.put(String.valueOf(k), row.getCell(k).toString());
                }
            }
        }
        return list;
    }

    /**
     * 导出excel表格文件(需要jxl.jar包)
     * @param list : 数据
     * @param titles : excel第一行名称
     * @param titleCode : Map的key
     * @param fileName : 文件名
     * @param response
     */
    public static void exportExcelToHtml(List<Map<String, Object>> list, String[] titles, String[] titleCode,
                                         String fileName, HttpServletResponse response) {
        exportExcelToHtml(list, titles, titleCode, fileName, SHEET_NAME, response);
    }

    /**
     * 导出excel表格文件(需要jxl.jar包)
     * @param list 数据
     * @param titles excel第一行名称
     * @param titleCode Map的key
     * @param fileName 文件名
     * @param sheetName sheet名
     * @param response
     */
    public static void exportExcelToHtml(List<Map<String, Object>> list, String[] titles, String[] titleCode,
                                         String fileName, String sheetName, HttpServletResponse response) {
        WritableWorkbook workbook = null;
        OutputStream os = null;
        try {
            response.setContentType("application/msexcel;charset=utf-8");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8") + ".xls");

            os = response.getOutputStream();
            workbook = jxl.Workbook.createWorkbook(os);
            // 创建新的一页
            WritableSheet sheet = workbook.createSheet(sheetName, 0);//设置页的名字
            sheet.getSettings().setDefaultColumnWidth(20);//设置默认列宽
            for (int i = 0; i < titles.length; i++) {
                Label label = new Label(i, 0, titles[i]);
                sheet.addCell(label);
            }

            if (null == list || list.size() == 0) {
                workbook.write();
                return;
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            // 表格添加数据
            for (int i = 0; i < list.size(); i++) { // 一行
                Map<String, Object> map = list.get(i);
                for (int j = 0; j < titles.length; j++) {
                    Label label = null;
                    Object param = map.get(titleCode[j]);
                    if (map.get(titleCode[j]) != null || titleCode[j] != null) {
                        jxl.write.Number number = null;
                        if (param instanceof Integer) {
                            int value = ((Integer) param).intValue();
                            number = new jxl.write.Number(j, i + 1, value);
                            sheet.addCell(number);
                        } else if (param instanceof String) {
                            String s = (String) param;
                            if ("price".equals(titleCode[j]) || "chrate".equals(titleCode[j])) {
                                int n = Integer.parseInt(s);
                                number = new jxl.write.Number(j, i + 1, n);
                                sheet.addCell(number);
                            } else {
                                label = new Label(j, i + 1, s);
                                sheet.addCell(label);
                            }
                        } else if (param instanceof Double) {
                            double d = ((Double) param).doubleValue();
                            number = new jxl.write.Number(j, i + 1, d);
                            sheet.addCell(number);
                        } else if (param instanceof Float) {
                            float f = ((Float) param).floatValue();
                            number = new jxl.write.Number(j, i + 1, f);
                            sheet.addCell(number);
                        } else if (param instanceof Long) {
                            long l = ((Long) param).longValue();
                            number = new jxl.write.Number(j, i + 1, l);
                            sheet.addCell(number);
                        } else if (param instanceof BigDecimal) {
                            long b = ((BigDecimal) param).longValue();
                            number = new jxl.write.Number(j, i + 1, b);
                            sheet.addCell(number);
                        } else if (param instanceof Date) {
                            Date date = (Date) param;
                            String newDate = dateFormat.format(date);
                            label = new Label(j, i + 1, newDate);
                            sheet.addCell(label);
                        }
                    } else {
                        label = new Label(j, i + 1, "");
                        sheet.addCell(label);
                    }
                }
            }
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param os 输出流
     */
    public static void axleDraw(List<Map<String, String>> dataSource, OutputStream os) {
        axleDraw(dataSource, null, os, SHEET_NAME, new ArrayList<>());
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param os 输出流
     * @param sheetName sheet名称
     */
    public static void axleDraw(List<Map<String, String>> dataSource, OutputStream os,
                                String sheetName) {
        axleDraw(dataSource, null, os, sheetName, new ArrayList<>());
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param os 输出流
     * @param titles 初始表头数据
     */
    public static void axleDraw(List<Map<String, String>> dataSource, OutputStream os,
                                List<String[]> titles) {
        axleDraw(dataSource, null, os, SHEET_NAME, titles);
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param os 输出流
     * @param sheetName sheet名称
     * @param titles 初始表头数据
     */
    public static void axleDraw(List<Map<String, String>> dataSource, OutputStream os,
                                String sheetName, List<String[]> titles) {
        axleDraw(dataSource, null, os, sheetName, titles);
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param os 输出流
     */
    public static void axleDraw(List<Map<String, String>> dataSource, Map<String, Object> remarkSource,
                                OutputStream os) {
        axleDraw(dataSource, remarkSource, os, SHEET_NAME, new ArrayList<>());
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param os 输出流
     * @param sheetName sheet名称
     */
    public static void axleDraw(List<Map<String, String>> dataSource, Map<String, Object> remarkSource,
                                OutputStream os, String sheetName) {
        axleDraw(dataSource, remarkSource, os, sheetName, new ArrayList<>());
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param os 输出流
     * @param titles 初始表头数据
     */
    public static void axleDraw(List<Map<String, String>> dataSource, Map<String, Object> remarkSource,
                                OutputStream os, List<String[]> titles) {
        axleDraw(dataSource, remarkSource, os, SHEET_NAME, titles);
    }

    /**
     * 生成单sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param os 输出流
     * @param sheetName sheet名称
     * @param titles 初始表头数据
     */
    public static void axleDraw(List<Map<String, String>> dataSource, Map<String, Object> remarkSource,
                                OutputStream os, String sheetName, List<String[]> titles) {
        Map<String, List<String[]>> titles_map = new HashMap<>();
        titles_map.put(sheetName, titles);
        Map<String, List<Map<String, String>>> dataSource_map = new HashMap<>();
        dataSource_map.put(sheetName, dataSource);
        Map<String, Map<String, Object>> remarkSource_map = new HashMap<>();
        remarkSource_map.put(sheetName, remarkSource);
        axleDraw(dataSource_map, remarkSource_map, os, new String[]{sheetName}, titles_map);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, OutputStream os) {
        Map<String, List<String[]>> titles = new HashMap<>();
        String[] sheetNames = new String[dataSource.size()];
        int i = 0;
        for (String key : dataSource.keySet()) {
            sheetNames[i++] = key;
            titles.put(key, new ArrayList<>());
        }
        axleDraw(dataSource, null, os, sheetNames, titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param sheetNames sheet名称
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, OutputStream os,
                                String[] sheetNames) {
        Map<String, List<String[]>> titles = new HashMap<>();
        for (String sheetName : sheetNames) {
            titles.put(sheetName, new ArrayList<>());
        }
        axleDraw(dataSource, null, os, sheetNames, titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param titles 初始表头数据
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, OutputStream os,
                                Map<String, List<String[]>> titles) {
        axleDraw(dataSource, null, os, (String[]) dataSource.keySet().toArray(), titles);
    }

    /**
     * 生成多sheet Excel
     *
     * @param dataSource 源数据
     * @param sheetNames sheet名称
     * @param titles     初始表头数据
     * @param os         输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, OutputStream os,
                                String[] sheetNames, Map<String, List<String[]>> titles) {
        axleDraw(dataSource, null, os, sheetNames, titles);
    }

    /**
     * 生成单/多sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                OutputStream os) {
        Map<String, List<String[]>> titles = new HashMap<>();
        String[] sheetNames = new String[dataSource.size()];
        int i = 0;
        for (String key : dataSource.keySet()) {
            sheetNames[i++] = key;
            titles.put(key, new ArrayList<>());
        }
        axleDraw(dataSource, remarkSource, os, sheetNames, titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param sheetNames sheet名称
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                OutputStream os, String[] sheetNames) {
        Map<String, List<String[]>> titles = new HashMap<>();
        for (String sheetName : sheetNames) {
            titles.put(sheetName, new ArrayList<>());
        }
        axleDraw(dataSource, remarkSource, os, sheetNames, titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param titles 初始表头数据
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                OutputStream os, Map<String, List<String[]>> titles) {
        axleDraw(dataSource, remarkSource, os, (String[]) dataSource.keySet().toArray(), titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param sheetNames sheet名称
     * @param titles 初始表头数据
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                OutputStream os, String[] sheetNames, Map<String, List<String[]>> titles) {
        Workbook wb = createWorkbook(sheetNames, titles);
        axleDraw(dataSource, remarkSource, os, wb, titles);
    }

    /**
     * 生成多sheet Excel
     * @param dataSource 源数据
     * @param remarkSource 坐标map
     * @param wb 需要标注的工作簿
     * @param titles 初始表头数据
     * @param os 输出流
     */
    public static void axleDraw(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                OutputStream os, Workbook wb, Map<String, List<String[]>> titles) {
        //OutputStream os = setResponseHeader(response,fileName);
        try {
            createFixationSheet(dataSource, remarkSource, os, wb, titles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //os.close();
    }

    /**
     * 生成Excel
     * @param dataSource 无表头源数据
     * @param remarkSource 标记map key: 横坐标,纵坐标  value:错误备注
     * @param os 输出流
     * @param wb 需要标注的工作簿
     * @param titles 初始表头数据
     * @throws IOException
     */
    private static void createFixationSheet(Map<String, List<Map<String, String>>> dataSource, Map<String, Map<String, Object>> remarkSource,
                                            OutputStream os, Workbook wb, Map<String, List<String[]>> titles) throws IOException {
        CellStyle style = createCellRedStyle(wb); // 样式对象
        for (String sheetKey : dataSource.keySet()) {
            if (!containsSheet(wb, sheetKey)) {
                createSheet(wb, sheetKey);
            }
            Sheet sheet = wb.getSheet(sheetKey);
            List<Map<String, String>> list = dataSource.get(sheetKey);
            for (int i = 0; !list.isEmpty() && i < list.size(); i++) {
                int rowNum;
                if (titles.containsKey(sheetKey)) {
                    rowNum = i + titles.get(sheetKey).size();
                } else {
                    rowNum = i;
                }
                Map<String, String> rowData = list.get(i);
                Row row = sheet.createRow(rowNum);
                for (String key : rowData.keySet()) {
                    Cell cell = row.createCell(Integer.valueOf(key));
                    cell.setCellValue(rowData.get(key));
                    if (null != remarkSource && !remarkSource.isEmpty() && remarkSource.containsKey(sheetKey)) {
                        Map<String, Object> map = remarkSource.get(sheetKey);
                        if (!map.isEmpty() && map.containsKey(rowNum + "," + key)) {
                            cell.setCellStyle(style);
                        }
                    }
                }
            }
        }
        wb.write(os);
        os.flush();
        os.close();
    }

    /**
     * 设置下载response属性
     * @param response 响应
     * @param fileName 文件名
     * @return OutputStream
     * @throws IOException io流异常
     */
    private static OutputStream setResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/msexcel;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName, "utf-8") + ".xls");
        return response.getOutputStream();
    }

    /**
     * 创建一个工作簿模板
     * @return Workbook
     */
    public static Workbook createWorkbook() {
        return createWorkbook(SHEET_NAME, new ArrayList<>());
    }

    /**
     * 创建一个工作簿模板
     * @param sheetName sheet名称
     * @return Workbook
     */
    public static Workbook createWorkbook(String sheetName) {
        return createWorkbook(sheetName, new ArrayList<>());
    }

    /**
     * 创建一个工作簿模板
     * @param titles 初始表头数据
     * @return Workbook
     */
    public static Workbook createWorkbook(List<String[]> titles) {
        return createWorkbook(SHEET_NAME, titles);
    }

    /**
     * 创建一个工作簿模板
     * @param sheetName sheet名称
     * @param titles 初始表头数据
     * @return Workbook
     */
    public static Workbook createWorkbook(String sheetName, List<String[]> titles) {
        Map<String, List<String[]>> map = new HashMap<>();
        map.put(sheetName, titles);
        return createWorkbook(new String[]{sheetName}, map);
    }

    /**
     * 创建多个工作簿模板
     * @param sheetNames sheet名称数组
     * @return Workbook
     */
    public static Workbook createWorkbook(String[] sheetNames) {
        Map<String, List<String[]>> map = new HashMap<>();
        for (String sheetName : sheetNames) {
            map.put(sheetName, new ArrayList<>());
        }
        return createWorkbook(sheetNames, map);
    }

    /**
     * 创建多个工作簿模板
     * @param titles 初始表头数据
     * @return Workbook
     */
    public static Workbook createWorkbook(Map<String, List<String[]>> titles) {
        String[] sheetNames = new String[titles.size()];
        int i = 0;
        for (String key : titles.keySet()) {
            sheetNames[i++] = key;
        }
        return createWorkbook(sheetNames, titles);
    }

    /**
     * 创建多个工作簿模板
     * @param sheetName sheet名称
     * @param titles 初始表头数据
     * @return Workbook
     */
    public static Workbook createWorkbook(String[] sheetName, Map<String, List<String[]>> titles) {
        // 创建工作薄
        Workbook wb = new HSSFWorkbook();
        // 在工作薄上建一张工作表
        createSheet(wb, sheetName, titles);
        return wb;
    }

    /**
     * 创建sheet
     *
     * @param wb        工作簿
     * @param sheetName sheet名称
     */
    public static void createSheet(Workbook wb, String sheetName) {
        wb.createSheet(sheetName);
    }

    /**
     * 创建sheet
     * @param wb 工作簿
     * @param sheetNames sheet名称
     * @param titles 初始表头数据
     */
    public static void createSheet(Workbook wb, String[] sheetNames, Map<String, List<String[]>> titles) {
        CellStyle cellStyle = createFontTextStyle(wb);
        for (String sheetName : sheetNames) {
            Sheet sheet = wb.createSheet(sheetName);
            if (titles.containsKey(sheetName)) {
                List<String[]> title = titles.get(sheetName);
                if (!titles.isEmpty()) {
                    for (int i = 0; i < title.get(0).length; i++) {
                        sheet.setDefaultColumnStyle(i, cellStyle);
                        //设置列宽
                        sheet.setColumnWidth(i, 20 * 15 * 2 * 13);
                    }
                    for (int i = 0; i < title.size(); i++) {
                        //创建列标题行
                        Row title_row = sheet.createRow(i);
                        //新增设置为文本格式
                        //创建示例数据行
                        for (int j = 0; j < title.get(i).length; j++) {
                            Cell cell = title_row.createCell(j);
                            cell.setCellValue(title.get(i)[j]);
                        }
                    }
                }
            }
        }
    }

    /**
     * 设置文本框属性——标红
     * @param wb Excel工作簿
     * @return CellStyle
     */
    private static CellStyle createCellRedStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle(); // 样式对象
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);// 设置前景填充样式
        style.setFillForegroundColor(HSSFColor.RED.index);// 前景填充色
        return style;
    }

    /**
     * 设置文本框属性——文本格式
     * @param wb Excel工作簿
     * @return CellStyle
     */
    private static CellStyle createFontTextStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        cellStyle.setDataFormat(format.getFormat("@"));
        return cellStyle;
    }

    public static Boolean containsSheet(Workbook wb, String sheetName) {
        return wb.getSheet(sheetName) != null;
    }

}
