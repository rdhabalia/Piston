/**
 * Copyright 2015 Rajan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.plugin.excel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidation.ErrorStyle;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationConstraint.ValidationType;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;

import com.plugin.excel.types.ExcelCell;
import com.plugin.excel.types.ExcelFormat;
import com.plugin.excel.types.ExcelGenPluginConfig;
import com.plugin.excel.types.XsdBaseType;

/**
 * It helps to create Excel file generation and formating.
 * 
 * @author rdhabal
 *
 */
public class ExcelFileHelper {

    /**
     * It creates a workbook for given excel-sheet data
     * 
     * @param directory
     * @param fileName
     * @param sheets
     */
    static int hiddenSheetCount = 0;
    static Map<String, Integer> sheetCount = new HashMap<String, Integer>(); // as Excel:POI can't store bigger name as
                                                                             // sheet-name, we are creating index
    static Map<StyleKey, CellStyle> styleCache = new HashMap<StyleKey, CellStyle>();
    private static final ExcelGenPluginConfig excelConfig = ExcelGenPluginConfig.config();
    private static final Logger log = Logger.getLogger(ExcelFileHelper.class);

    public static void writeFile(String directory, String fileName, Map<String, List<List<ExcelCell>>> sheets,
            int rowHeight) {
        writeFile(directory, fileName, sheets, rowHeight, rowHeight);
    }

    public static void writeFile(String directory, String fileName, Map<String, List<List<ExcelCell>>> sheets,
            int headerRowHeight, int commentRowHeight) {

        if (StringUtils.isNotBlank(directory) && StringUtils.isNotBlank(fileName) && sheets != null
                && !sheets.isEmpty()) {

            SXSSFWorkbook workbook = new SXSSFWorkbook();

            Font invisibleFont = workbook.createFont();

            for (Entry<String, List<List<ExcelCell>>> entry : sheets.entrySet()) {
                // TODO: remove and logging
                // log.info("writeFile","Started writing sheet: "+entry.getKey());

                SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(entry.getKey());
                int totalColumn = 0;
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    int rowNumber = 0;
                    Font dataFont = null;
                    for (List<ExcelCell> rows : entry.getValue()) {
                        // Row row = sheet.getRow(rowNumber)!=null ? sheet.getRow(rowNumber) : rowMap.get(rowNumber);
                        Row row = sheet.createRow(rowNumber);
                        int rowHeight = rowNumber == 0 ? headerRowHeight : commentRowHeight;
                        if (rowNumber == 0 || rowNumber == 1) {
                            if (rowHeight > 0) {
                                row.setHeight((short) rowHeight);
                            }
                            addDataValidation(rowNumber, sheet);
                        }
                        rowNumber++;
                        if (rows != null && !rows.isEmpty()) {
                            int cellNum = 0;
                            Font font = null;
                            if (rowNumber > 3 && dataFont != null) {
                                font = dataFont;
                            } else {
                                font = workbook.createFont();
                                dataFont = font;
                            }
                            // as each row requires different syle with separate font
                            Map<IndexedColors, CellStyle> s_cellStyle = new HashMap<IndexedColors, CellStyle>();
                            for (ExcelCell cellValue : rows) {
                                Cell cell = row.createCell(cellNum);
                                updateCell(cell, cellValue, s_cellStyle, workbook, font, invisibleFont);
                                ++cellNum;
                            }
                            totalColumn = cellNum;
                        }
                        if (rowNumber == 2) {/*
                                              * auto size after DOCUMENTATION-ROW (row=2) so, we don't have to do
                                              * multiple times
                                              */
                            autoSize(sheet, totalColumn, false);
                            // rowMap = createRows(workbook, sheet, rowNumber+1, excelConfig.getMaxInputRows());
                        }
                    }

                }
                autoSize(sheet, totalColumn, true);
            }

            // addMetaSheet(workbook);

            writeWorkBook(directory, fileName, workbook);
        }

    }

    private static void autoSize(Sheet sheet, int totalColumn, boolean setWrap) {

        if (sheet != null) {
            for (int i = 0; i < totalColumn; i++) {
                sheet.autoSizeColumn((short) (i));
            }
        }

    }

    /**
     * It helps to update cell and format the excell based on the formatting defined in ExcelCell.{@link ExcelFormat}
     * 
     * @param cell
     * @param excell
     * @param style
     * @param font
     */
    private static void updateCell(Cell cell, ExcelCell excell, Map<IndexedColors, CellStyle> s_cellStyle,
            Workbook workbook, Font font, Font invisibleFont) {
        if (excell != null) {

            // [1] format cell
            formatCell(workbook, cell, excell, s_cellStyle, font, invisibleFont);

            // [2] set enum
            if (!excell.isConsiderEnum()) {
                if (StringUtils.isNotBlank(excell.getDisplayText())) {
                    cell.setCellValue(excell.getDisplayText());
                }
                if (!excell.isMultiSelect() && excell.isNumberValidation()) {
                    addNumberValidation(cell);
                }
            } else {
                String[] list = (String[]) excell.getRestriction().getEnumValues()
                        .toArray(new String[excell.getRestriction().getEnumValues().size()]);

                SXSSFSheet sheet = (SXSSFSheet) cell.getSheet();

                DataValidationHelper dvHelper = sheet.getDataValidationHelper();
                DataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                        .createExplicitListConstraint(list);
                CellRangeAddressList regions = new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(),
                        cell.getColumnIndex(), cell.getColumnIndex());
                DataValidation dataValidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.createErrorBox("Not Applicable", "Can't change the value");
                dataValidation.setShowErrorBox(true);

                try {
                    if (isValidEnumList(list)) {
                        sheet.addValidationData(dataValidation);
                    } else {
                        Sheet hidden = null;
                        String hiddenName = "hidden" + getHiddenIndex(excell.getReferenceText());
                        Workbook wBook = cell.getSheet().getWorkbook();
                        if (cell.getSheet().getWorkbook().getSheet(hiddenName) != null) {
                            hidden = wBook.getSheet(hiddenName);
                        } else {
                            hidden = wBook.createSheet(hiddenName);

                            for (int i = 0, length = list.length; i < length; i++) {
                                String name = list[i];
                                Row row = hidden.createRow(i);
                                Cell cell1 = row.createCell(0);
                                cell1.setCellValue(name);
                            }
                            Name namedCell = hidden.getWorkbook().getName(hiddenName);
                            namedCell = namedCell != null ? namedCell : hidden.getWorkbook().createName();
                            namedCell.setNameName(hiddenName);
                            namedCell.setRefersToFormula(hiddenName + "!$A$1:$A$" + list.length);
                        }

                        dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(hiddenName);
                        dataValidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
                        dataValidation.createErrorBox("Not Applicable", "Can't change the value");
                        dataValidation.setShowErrorBox(true);

                        cell.getSheet().addValidationData(dataValidation);
                        wBook.setSheetHidden(wBook.getSheetIndex(hidden), true);

                    }

                } catch (Exception e) {
                    String msg = "Excel creation failed while building cell: " + excell.getDisplayText();
                    throw new IllegalStateException(msg, e);
                }

                // cell.setCellValue(excelConfig.getDropDownMsg());
            }

        }

    }

    private static void formatCell(Workbook workbook, Cell cell, ExcelCell excell,
            Map<IndexedColors, CellStyle> s_cellStyle, Font font, Font invisibleFont) {

        if (excell.getFormat() != null) {

            ExcelFormat format = excell.getFormat();

            CellStyle style = s_cellStyle.get(format.getBackgroundColor());

            if (format.isDate()) {
                // for date create a new style
                style = getDateStyle("date", cell.getSheet(), font);
                XSSFCreationHelper createHelper = (XSSFCreationHelper) cell.getSheet().getWorkbook()
                        .getCreationHelper();
                style.setDataFormat(createHelper.createDataFormat().getFormat("MMMM dd, yyyy"));
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                font.setBold(false);
                font.setFontHeightInPoints((short) 12);
                style.setFont(font);
                cell.setCellValue(new Date());
            }

            if (style == null) {
                style = workbook.createCellStyle();
                s_cellStyle.put(format.getBackgroundColor(), style);
            }

            if (format.getAlignment() > 0) {
                style.setAlignment(format.getAlignment());
            }
            if (format.getBackgroundColor() != null && !IndexedColors.WHITE.equals(format.getBackgroundColor())) {
                style.setFillForegroundColor(format.getBackgroundColor().getIndex());
                style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            }
            if (format.getTextColor() != null) {
                font.setColor(format.getTextColor().getIndex());
                style.setFont(font);
            }
            if (format.isBold()) {
                font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            }
            if (format.getFontHeight() > 0) {
                font.setFontHeightInPoints(format.getFontHeight());
            }
            if (format.isWrapText()) {
                style.setWrapText(true);
            }
            style.setFont(font);
            if (format.isHideText()) {
                invisibleFont.setColor(IndexedColors.WHITE.getIndex());
                style.setFont(invisibleFont);
            }
            cell.setCellStyle(style);

        } else {
            // Let's set default formatting for free text cell
            IndexedColors defaultStyle = IndexedColors.AUTOMATIC; // we are using this index
            CellStyle style = s_cellStyle.get(defaultStyle);
            if (style == null) {
                style = workbook.createCellStyle();
                s_cellStyle.put(defaultStyle, style);
            }
            style.setWrapText(true);
            cell.setCellStyle(style);

        }

    }

    private static int getHiddenIndex(String referenceText) {

        if (sheetCount.get(referenceText) == null) {
            sheetCount.put(referenceText, hiddenSheetCount++);
        }

        return sheetCount.get(referenceText);
    }

    private static CellStyle getDateStyle(String style, Sheet sheet, Font font) {
        if (StringUtils.isNotBlank(style) && sheet != null) {
            StyleKey cache = new StyleKey(style, sheet.getSheetName());
            if (styleCache.get(cache) == null) {
                CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
                font.setFontHeight((short) 10);
                cellStyle.setFont(font);
                styleCache.put(cache, cellStyle);
            }
            return styleCache.get(cache);
        }
        return null;
    }

    private static boolean isValidEnumList(String[] list) {

        if (list != null && list.length > 0) {
            int charLength = 0;
            for (int i = 0; i < list.length; i++) {
                charLength += list[i].length();
            }
            return charLength < 255;
        }

        return true;
    }

    /**
     * It generates new Excel file at given locaiton
     * 
     * @param directory
     * @param fileName
     * @param workbook
     */
    private static void writeWorkBook(String directory, String fileName, Workbook workbook) {

        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(directory + "/" + fileName);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            String msg = "File creation fialed for dir,fileName: " + directory + "," + fileName + e.getMessage();
            throw new IllegalStateException(msg, e);
        }

    }

    private static void addDataValidation(int rowNum, SXSSFSheet sheet) {

        String[] displayNameList = new String[] { "" };
        DataValidationHelper dvHelper = sheet.getDataValidationHelper();
        DataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
                .createExplicitListConstraint(displayNameList);
        CellRangeAddressList regions = new CellRangeAddressList(rowNum, rowNum, 0, 1000);
        DataValidation dataValidation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, regions);
        dataValidation.setSuppressDropDownArrow(false);
        dataValidation.createErrorBox("Not Applicable", "Can't change the value");
        dataValidation.setShowErrorBox(true);
        sheet.addValidationData(dataValidation);
    }

    private static void addNumberValidation(Cell cell) {

        if (cell != null) {

            Sheet sheet = cell.getSheet();
            DataValidationHelper dvHelper = sheet.getDataValidationHelper();
            XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createNumericConstraint(
                    ValidationType.DECIMAL, DVConstraint.OperatorType.BETWEEN, "1.00", "1000000000000.00");
            CellRangeAddressList addressList = new CellRangeAddressList(cell.getRowIndex(), cell.getRowIndex(),
                    cell.getColumnIndex(), cell.getColumnIndex());
            XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setErrorStyle(ErrorStyle.STOP);
            validation.createErrorBox("Error", "Only numeric values are allowed");
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);
        }

    }

    public static boolean isNumber(String type) {

        return StringUtils.isNotBlank(type) && (XsdBaseType.DECIMAL.equalsIgnoreCase(type)
                || XsdBaseType.DOUBLE.equalsIgnoreCase(type) || XsdBaseType.INTEGER.equalsIgnoreCase(type)) ? true
                        : false;
    }

    private static void addMetaSheet(SXSSFWorkbook workbook) {

        SXSSFSheet instructionSheet = (SXSSFSheet) workbook.createSheet("Instrunction");
        SXSSFSheet sheet = (SXSSFSheet) workbook.getSheet("Other");
        ExcelUtil.copySheets(instructionSheet, sheet);

    }

}
