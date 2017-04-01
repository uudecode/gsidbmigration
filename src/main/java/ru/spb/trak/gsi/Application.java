package ru.spb.trak.gsi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by trak on 24.01.2017.
 */
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    public static final String CONFIG_FILE = "properties";
    private static Properties properties = new Properties();
    private static Workbook outputBook = new XSSFWorkbook();
    private static Sheet sheet = outputBook.createSheet("Queries Info");
    public static void main(String[] args) {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (Exception e) {
            LOGGER.error("Can't read configuration file {}, got error: {}", CONFIG_FILE, e);
            System.exit(-1);
        }

        LOGGER.info("Wonderfukk! We started at least!!!");

        prepareOutputExcel();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(properties.getProperty("xml.dir")), properties.getProperty("xml.mask"))) {
            for (Path entry : stream) {
                LOGGER.debug("Proccessing file is:[{}]",entry.toString());
                try {
                    CheckFile checkFile =  new CheckFile(properties,entry,sheet);
                    checkFile.staxParse();
                } catch (Exception e) {
                    LOGGER.error("Got error while proccesing file:{} {}",entry.toString(),  e.getMessage());
                }
            }
        } catch (Exception e) {
            System.exit(-2);
        }
        try {

            Desktop.getDesktop().open(new File(properties.getProperty("output.excel")));
        } catch (Exception e) {
            LOGGER.error("Can't open excel");
        }
        LOGGER.info("Lol, I came.");
    }

    private static void prepareOutputExcel() {

        Font headerFont= outputBook.createFont();
        headerFont.setFontHeightInPoints((short)14);
        headerFont.setFontName("Century Gothic");
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        headerFont.setBold(true);
        headerFont.setItalic(false);

        int rowIndex = 0;
        Row headRow = sheet.createRow(rowIndex++);
        CellStyle headerStyle = outputBook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());

        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());


        CellStyle ordinaryStyle = outputBook.createCellStyle();
        ordinaryStyle.setFillBackgroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        ordinaryStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());

        Cell fileNameCell = headRow.createCell(0);
        fileNameCell.setCellValue(new XSSFRichTextString("File name"));
        fileNameCell.setCellStyle(headerStyle);

        Cell tableNameCell = headRow.createCell(1);
        tableNameCell.setCellValue(new XSSFRichTextString("Table name"));
        tableNameCell.setCellStyle(headerStyle);

        Cell queryTypeCell = headRow.createCell(2);
        queryTypeCell.setCellValue(new XSSFRichTextString("Query type"));
        queryTypeCell.setCellStyle(headerStyle);

        Cell querySubTypeCell = headRow.createCell(3);
        querySubTypeCell.setCellValue(new XSSFRichTextString("Query Subtype"));
        querySubTypeCell.setCellStyle(headerStyle);

        Cell queryStatusCell = headRow.createCell(4);
        queryStatusCell.setCellValue(new XSSFRichTextString("Status"));
        queryStatusCell.setCellStyle(headerStyle);

        Cell queryTimeCell = headRow.createCell(5);
        queryTimeCell.setCellValue(new XSSFRichTextString("Execution time"));
        queryTimeCell.setCellStyle(headerStyle);

        Cell queryErrorCell = headRow.createCell(6);
        queryErrorCell.setCellValue(new XSSFRichTextString("Error text"));
        queryErrorCell.setCellStyle(headerStyle);
    }

}
