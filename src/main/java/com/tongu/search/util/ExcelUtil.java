package com.tongu.search.util;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Excel工具类
 * @author ：wangjf
 * @date ：2020/3/30 20:22
 * @description：provider-com
 * @version: v1.1.0
 */
@Slf4j
public class ExcelUtil {

    @Data
    public static class ExcelEntity {
        /**
         * 导出Excel的目录
         */
        private String fileFolder;
        /**
         * 导出Excel的名称
         */
        private String fileName;
        /**
         * 导出Excel的sheet页名称
         */
        private String sheetName;
        /**
         * 表头
         */
        private List<Object> headers = Lists.newArrayList();
        /**
         * 表内容
         */
        private List<List<Object>> content = Lists.newArrayList();
    }

    /**
     * 导出Excel
     * @param entity
     */
    public static File createReport(ExcelEntity entity) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(entity.getSheetName());
        CellStyle style = workbook.createCellStyle();
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        //style.setAlignment(HorizontalAlignment.CENTER);
        //style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        //font.setFontName("宋体");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);

        Row headRow = sheet.createRow(0);
        for(int i = 0; i < entity.getHeaders().size(); i ++) {
            Cell cell = headRow.createCell(i, CellType.STRING);
            cell.setCellValue(entity.getHeaders().get(i).toString());
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 256*15);
        }

        for(int i = 0; i < entity.getContent().size(); i ++) {
            Row row = sheet.createRow(i+1);
            row.setHeightInPoints(15);
            List<Object> rowContent = entity.getContent().get(i);
            for(int j = 0; j < rowContent.size(); j ++) {
                Cell cell = row.createCell(j, CellType.STRING);
                cell.setCellStyle(style);
                Object value = rowContent.get(j);
                if(value == null) {
                    continue;
                }
                if(value instanceof BigDecimal) {
                    cell.setCellValue(((BigDecimal) value).toPlainString());
                    cell.setCellType(CellType.STRING);
                } if(value instanceof Long) {
                    cell.setCellValue(value.toString());
                    cell.setCellType(CellType.STRING);
                } else{
                    cell.setCellValue(value.toString());
                    cell.setCellType(CellType.STRING);
                }
            }
        }

        File file = new File(entity.getFileFolder());
        if(!file.exists()) {
            file.mkdirs();
        }
        String path = Paths.get(entity.getFileFolder(), entity.getFileName()).toString();
        file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            log.error("导出出错！{}", e);
            throw new RuntimeException(e.getMessage());
        }

        return file;
    }

    @FunctionalInterface
    public interface ExcelConsumer {
        /**
         * 下载文件
         * @param workBook
         */
        void write(Workbook workBook);
    }

    /**
     * 通过模板导出文件
     * @param templateName 模板名称
     * @param filePath 导出文件路径
     * @param fileName 导出文件名称
     * @param consumer 写excel的操作
     * @return
     */
    public static File createReport(String templateName, String filePath, String fileName, ExcelConsumer consumer) {
        OutputStream out = null;
        FileInputStream fileInputStream = null;
        File file = null;
        try {
            InputStream templateInputStream = new ClassPathResource(MessageFormat.format("excel/{0}", templateName)).getInputStream();
            file = createFileFromTemplate(templateInputStream, filePath, fileName);
            fileInputStream = new FileInputStream(file);
            InputStream inputStream = FileMagic.prepareToCheckMagic(fileInputStream);
            Workbook workBook = null;
            if(FileMagic.valueOf(inputStream).equals(FileMagic.OLE2)) {
                workBook = new HSSFWorkbook(inputStream);
            } else {
                workBook = new XSSFWorkbook(inputStream);
            }
            consumer.write(workBook);
            out = new FileOutputStream(file);
            workBook.write(out);
        } catch (Exception e) {
            log.error("导出出错！{}", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                fileInputStream.close();
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                log.error("导出出错！{}", e);
                throw new RuntimeException(e.getMessage());
            }
        }
        return file;
    }

    /**
     * 通过模板文件创建新文件
     * @param templateInputStream
     * @param filePath
     * @param fileName
     * @return
     */
    private static File createFileFromTemplate(InputStream templateInputStream, String filePath, String fileName) {
        File file = new File(filePath);
        if(!file.exists()) {
            file.mkdirs();
        }
        String path = Paths.get(filePath, fileName).toString();
        file = new File(path);
        if(file.exists()) {
            file.delete();
        }
        // 拷贝模板文件
        FileUtil.copyFile(templateInputStream, file);
        return file;
    }

    /**
     * 向excel的sheet某行某列写入值
     *
     * @param sheet
     * @param rowNumber
     * @param colNumber
     * @param value
     */
    public static void write(Sheet sheet, Integer rowNumber, Integer colNumber, Object value) {
        if(value == null) {
            return;
        }
        Row row = sheet.getRow(rowNumber);
        Cell cell = row.getCell(colNumber);
        if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Date) {
            cell.setCellValue(DateUtils.formatDate((Date) value, "yyyy/MM/dd"));
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 插入行
     * @param sheet
     * @param startRowNumber 起始行位置
     * @param rows 行数
     * @param cols 列数
     */
    public static void insert(Sheet sheet, Integer startRowNumber, Integer rows, Integer cols) {
        if (sheet.getRow(startRowNumber) != null) {
            int lastRowNumber = sheet.getLastRowNum();
            sheet.shiftRows(startRowNumber, lastRowNumber, rows);
        }

        for(int i = 0; i < rows; i ++) {
            Row row = sheet.createRow(startRowNumber + i);
            row.setHeight((short) (24* 20));
            for(int j = 0; j < cols; j ++) {
                row.createCell((short) j);
            }
        }
    }

    /**
     * 插入（并复制样式）
     * @param sheet
     * @param startRowNumber
     * @param rows
     * @param cols
     * @param sourceRowNumber
     */
    public static void insert(Sheet sheet, Integer startRowNumber, Integer rows, Integer cols, Integer sourceRowNumber) {
        insert(sheet, startRowNumber, rows, cols);
        copy(sheet, sourceRowNumber, startRowNumber, rows);
    }

    /**
     * 复制行
     * @param sheet
     * @param sourceRowNumber 待复制的行
     * @param startRowNumber 目标行位置
     * @param rows 行数
     */
    public static void copy(Sheet sheet, Integer sourceRowNumber, Integer startRowNumber, Integer rows) {
        Row sourceRow = sheet.getRow(sourceRowNumber);
        for(int i = 0; i < rows; i ++) {
            Row destRow = sheet.getRow(startRowNumber + i);
            for (Iterator cellIt = sourceRow.cellIterator(); cellIt.hasNext();) {
                Cell sourceCell = (Cell) cellIt.next();
                Cell destCell = destRow.getCell(sourceCell.getColumnIndex());
                if(destCell == null) {
                    break;
                }
                destCell.setCellStyle(sourceCell.getCellStyle());
                destCell.setCellType(sourceCell.getCellTypeEnum());
            }
        }
    }

    /**
     * 删除行
     * @param sheet
     * @param startRowNumber 待删除的行
     */
    public static void delete(Sheet sheet, Integer startRowNumber) {
        if (sheet.getRow(startRowNumber) != null) {
            int lastRowNumber = sheet.getLastRowNum();
            sheet.shiftRows(startRowNumber + 1, lastRowNumber, -1);
        }
    }

    public static List<Object> readOneRow(Row row) {
        List<Object> list = Lists.newArrayList();
        int lastCellNum = row.getLastCellNum();
        for(int col = 0; col < lastCellNum; col++) {
            CellType cellTypeEnum = row.getCell(col).getCellTypeEnum();
            if(cellTypeEnum.equals(CellType.NUMERIC)) {
                list.add(String.valueOf(row.getCell(col).getNumericCellValue()));
            } else{
                list.add(row.getCell(col).getStringCellValue());
            }
        }
        return list;
    }

    /**
     * 读取excel
     * @param excelEntity
     */
    public static void read(ExcelEntity excelEntity) {
        FileInputStream fileInputStream = null;
        try {
            //fileInputStream = new ClassPathResource(MessageFormat.format(excelEntity.getFileFolder(), excelEntity.getContent())).getInputStream();
            String path = Paths.get(excelEntity.getFileFolder(), excelEntity.getFileName()).toString();
            fileInputStream = new FileInputStream(path);
            InputStream inputStream = FileMagic.prepareToCheckMagic(fileInputStream);
            Workbook workBook = null;
            if(FileMagic.valueOf(inputStream).equals(FileMagic.OLE2)) {
                workBook = new HSSFWorkbook(inputStream);
            } else {
                workBook = new XSSFWorkbook(inputStream);
            }
            Sheet sheet = workBook.getSheet(excelEntity.getSheetName());
            // 取head
            Row row = sheet.getRow(0);
            excelEntity.getHeaders().addAll(readOneRow(row));
            int lastRowNum = sheet.getLastRowNum();
            for(int i = 1; i < lastRowNum; i ++) {
                excelEntity.getContent().add(readOneRow(sheet.getRow(i)));
            }
        } catch (Exception e) {
            log.error("导入出错！{}", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                log.error("导入出错！{}", e);
                throw new RuntimeException(e.getMessage());
            }
        }

    }
}
