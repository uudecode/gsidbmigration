package ru.spb.trak.gsi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by trak on 24.01.2017.
 */
public class CheckFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckFile.class);
    private Path checkingFile;
    private Query currentQuery;
    private Sheet sheet;
    private Properties properties;

    public CheckFile(Properties properties, Path checkingFile, Sheet sheet) {
        LOGGER.debug("Next file is: {}", checkingFile);
        this.properties = properties;
        this.checkingFile = checkingFile;
        this.sheet = sheet;
    }

    public void staxParse() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(checkingFile.toFile()));
            boolean queryTextGoing = false;
            String tableName = new String();
            String queryType = new String();
            String querySubType = new String();
            String queryStatus = new String();
            String queryError = new String();
            double milliseconds = 0;

            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT: {
                        LOGGER.debug("START_ELEMENT: {} attr({})={} attr({})={}", reader.getLocalName(), reader.getAttributeName(0), reader.getAttributeValue(0), reader.getAttributeName(1), reader.getAttributeValue(1));
                        if ("geoQuery".equals(reader.getLocalName())) {
                            currentQuery = new GeoQuery(checkingFile.toString(), properties);
                            currentQuery.setTableName(tableName);
                            currentQuery.setQueryType(queryType);
                            querySubType = reader.getLocalName();
                        }
                        if ("dbQuery".equals(reader.getLocalName())) {
                            currentQuery = new DbQuery(checkingFile.toString(), properties);
                            currentQuery.setTableName(tableName);
                            currentQuery.setQueryType(queryType);
                            querySubType = reader.getLocalName();
                        }
                        if ("xmlQuery".equals(reader.getLocalName())) {
                            currentQuery = new XmlQuery(checkingFile.toString(), properties);
                            currentQuery.setTableName(tableName);
                            currentQuery.setQueryType(queryType);
                            querySubType = reader.getLocalName();
                        }
                        if ("xmlCommand".equals(reader.getLocalName())) {
                            currentQuery = new XmlCommand(checkingFile.toString(), properties);
                            currentQuery.setTableName(tableName);
                            currentQuery.setQueryType(queryType);
                            querySubType = reader.getLocalName();
                        }
                        if ("dbCommand".equals(reader.getLocalName())) {
                            currentQuery = new DbCommand(checkingFile.toString(), properties);
                            currentQuery.setTableName(tableName);
                            currentQuery.setQueryType(queryType);
                            querySubType = reader.getLocalName();
                        }
                        if ("var".equals(reader.getLocalName())) {
                            currentQuery.getVars().put(reader.getAttributeValue(0).toString(), ((null != reader.getAttributeValue(1)) && !"".equals(reader.getAttributeValue(1))) ? reader.getAttributeValue(1).toString() : "0");
                        }
                        if ("query".equals(reader.getLocalName())) {
                            queryTextGoing = true;
                        }
                        if ("data".equals(reader.getLocalName())) {
                            tableName = reader.getAttributeValue(0);
                        }
                        if ("select".equals(reader.getLocalName()) || "insert".equals(reader.getLocalName()) || "update".equals(reader.getLocalName()) || "delete".equals(reader.getLocalName())) {
                            queryType = reader.getLocalName();
                        }


                        break;
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        LOGGER.debug("characters: {}", reader.getText());
                        if (queryTextGoing) {
                            if (null != currentQuery.getOriginalQuery())
                                currentQuery.setOriginalQuery(currentQuery.getOriginalQuery() + reader.getText());
                            else
                                currentQuery.setOriginalQuery(reader.getText());
                        }

                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT: {
                        LOGGER.debug("END_ELEMENT: {}", reader.getLocalName());
                        if ("geoQuery".equals(reader.getLocalName()) || "dbQuery".equals(reader.getLocalName()) || "xmlQuery".equals(reader.getLocalName()) || "xmlCommand".equals(reader.getLocalName()) || "dbCommand".equals(reader.getLocalName())) {
                            queryTextGoing = false;
                            currentQuery.prepareQueryText();
                            try {
                                milliseconds = System.currentTimeMillis() ;
                                queryStatus = currentQuery.tryQuery();
                            } catch (Exception e) {
                                queryStatus = "ERROR";
                                queryError = e.getMessage();
                            }
                            milliseconds = System.currentTimeMillis() - milliseconds;
                            fillSheetRow(tableName, queryType, querySubType, queryStatus, queryError, milliseconds);
                            currentQuery = null;
                            queryError = null;
                            queryStatus = null;
                        }

                        break;
                    }
                }
            }
        } catch (FileNotFoundException fe) {
            LOGGER.error("Got common file error {} for file: {}", fe.getMessage(), checkingFile);
        } catch (XMLStreamException xe) {
            LOGGER.error("Got common xml error {} for file: {}", xe.getMessage(), checkingFile);
        } catch (Exception e2) {
            LOGGER.error("Got common error {} for file: {}", e2.getMessage(), checkingFile);

        }
    }

    private void fillSheetRow(String tableName, String queryType, String querySubType, String queryStatus, String queryError, double milliseconds) {

        Font defaultFont = sheet.getWorkbook().createFont();
        defaultFont.setFontHeightInPoints((short) 10);
        defaultFont.setFontName("Century Gothic");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        int rowNumber = sheet.getLastRowNum() + 1;
        Row headRow = sheet.createRow(rowNumber);

        CellStyle ordinaryStyle = sheet.getWorkbook().createCellStyle();

        if ("IGNORE".equals(queryStatus))
            ordinaryStyle.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
        else if ("ERROR".equals(queryStatus))
            ordinaryStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        else if ("OK".equals(queryStatus))
            ordinaryStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        else
            ordinaryStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());

        ordinaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ordinaryStyle.setFont(defaultFont);
        ordinaryStyle.setBorderBottom(BorderStyle.THIN);
        ordinaryStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        ordinaryStyle.setBorderLeft(BorderStyle.THIN);
        ordinaryStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        ordinaryStyle.setBorderRight(BorderStyle.THIN);
        ordinaryStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        ordinaryStyle.setBorderTop(BorderStyle.THIN);
        ordinaryStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        Cell fileNameCell = headRow.createCell(0);
        fileNameCell.setCellValue(new XSSFRichTextString(checkingFile.getFileName().toString()));
        fileNameCell.setCellStyle(ordinaryStyle);

        Cell tableNameCell = headRow.createCell(1);
        tableNameCell.setCellValue(new XSSFRichTextString(tableName));
        tableNameCell.setCellStyle(ordinaryStyle);

        Cell queryTypeCell = headRow.createCell(2);
        queryTypeCell.setCellValue(new XSSFRichTextString(queryType));
        queryTypeCell.setCellStyle(ordinaryStyle);

        Cell querySubTypeCell = headRow.createCell(3);
        querySubTypeCell.setCellValue(new XSSFRichTextString(querySubType));
        querySubTypeCell.setCellStyle(ordinaryStyle);

        Cell queryStatusCell = headRow.createCell(4);
        queryStatusCell.setCellValue(new XSSFRichTextString(queryStatus));
        queryStatusCell.setCellStyle(ordinaryStyle);

        Cell queryTimeCell = headRow.createCell(5);
        queryTimeCell.setCellValue(new XSSFRichTextString(Double.toString(milliseconds / 1000.0)));
        queryTimeCell.setCellStyle(ordinaryStyle);

        Cell queryErrorCell = headRow.createCell(6);
        queryErrorCell.setCellValue(new XSSFRichTextString(queryError));
        queryErrorCell.setCellStyle(ordinaryStyle);

        writeOutputExcel();
    }

    public void writeOutputExcel() {
        String outputExcel = properties.getProperty("output.excel");
        sheet.autoSizeColumn(0, false);
        sheet.autoSizeColumn(1, false);
        sheet.autoSizeColumn(2, false);
        sheet.autoSizeColumn(3, false);
        sheet.autoSizeColumn(4, false);
        sheet.autoSizeColumn(5, false);
        sheet.autoSizeColumn(6, false);

        try {
            sheet.getWorkbook().write(new FileOutputStream(outputExcel));
        } catch (Exception e) {
            LOGGER.error("Can't write output excel {}, got error: {}", outputExcel, e);
        }
    }
}
