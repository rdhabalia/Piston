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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;

import com.plugin.excel.type.node.XsdBaseType;
import com.plugin.excel.types.ExcelCell;
import com.plugin.excel.types.ExcelCellKey;
import com.plugin.excel.types.ExcelFormat;
import com.plugin.excel.types.ExcelSheet;
import com.plugin.excel.types.ProductWorkBook;
import com.plugin.excel.xsd.node.store.api.ExcelFormatterApi;
import com.plugin.excel.xsd.node.store.impl.RootNodeManager;

/**
 * 
 * @author rdhabal
 *
 */
public class ExcelFormatterApiImpl implements ExcelFormatterApi {

    private Map<String, Map<ExcelCellKey, ExcelCell>> CELL_INDEX = new HashMap<String, Map<ExcelCellKey, ExcelCell>>();
    private boolean initialized;

    @Autowired
    private RootNodeManager rootMgr;

    /**
     * It builds offline when it doesn't require to generate Excel
     * 
     */
    public void init() {

        if (!initialized) {
            Set<String> versions = rootMgr.getSupportedVersions();
            if (versions != null && !versions.isEmpty()) {
                for (String version : versions) {
                    generateProductExcelAndBuildIndex(version, "Root", null, false);
                }
            }
            initialized = true;
        }

    }

    public void generateProductExcel(String version, String root, String directory) {
        generateProductExcelAndBuildIndex(version, root, directory, true);
    }

    public void generateProductExcelAndBuildIndex(String version, String root, String directory,
            boolean isGenerateFile) {

        Map<String, ProductWorkBook> books = rootMgr.buildExcelWorkBook(version, root);
        if (books != null && !books.isEmpty()) {

            for (Entry<String, ProductWorkBook> book : books.entrySet()) {

                // {1} create one complete excel workbook.
                Map<String, List<List<ExcelCell>>> bookmap = new HashMap<String, List<List<ExcelCell>>>();
                ProductWorkBook productBook = book.getValue();

                String categoryName = productBook.getCategoryName();
                String excelFileName = categoryName + ".xlsx";

                if (productBook != null && !productBook.getSubCategorySheets().isEmpty()) {
                    // (1) get product attributes
                    ExcelSheet prodAttr = productBook.getProductAttributes();
                    // (2) get category attributes
                    ExcelSheet catAttr = productBook.getCaetgoryAttributes();
                    for (Entry<String, ExcelSheet> productSheet : productBook.getSubCategorySheets().entrySet()) {
                        // (3) get sub-category attributes
                        String sheetName = productSheet.getKey();
                        List<List<ExcelCell>> rows = createRows(version, prodAttr, catAttr);
                        //
                        buildIndex(version, categoryName, rows.get(0), sheetName);
                        bookmap.put(sheetName, rows);
                    }

                }
                // Generate file if it's invoked by Excel-Generator
                if (isGenerateFile) {
                    String targetDir = directory + "/" + version;
                    ExcelFileHelper.writeFile(targetDir, excelFileName, bookmap, 500, 500);
                }
            }
        }

    }

    public ExcelCell identifyCell(String version, String workbookName, String sheetName, String cellName) {

        if (StringUtils.isNotBlank(version) && StringUtils.isNotBlank(workbookName) && StringUtils.isNotBlank(sheetName)
                && StringUtils.isNotBlank(cellName)) {
            ExcelCellKey key = new ExcelCellKey(workbookName, sheetName, cellName);
            return CELL_INDEX.get(version) != null ? CELL_INDEX.get(version).get(key) : null;
        }

        return null;

    }

    public void buildIndex(String version, String workBookName, ExcelSheet excelSheet) {
        buildCellIndex(version, workBookName, excelSheet);
    }

    /**
     * It helps to build index for a given excel sheet
     * 
     * @param version
     * @param excelFileName
     * @param list
     * @param sheetName
     */
    private void buildIndex(String version, String excelFileName, List<ExcelCell> list, String sheetName) {

        ExcelSheet sheet = new ExcelSheet();
        sheet.setName(sheetName);
        sheet.getRows().add(list);
        buildIndex(version, excelFileName, sheet);
    }

    /**
     * It creates a list of excelRows from the product-attribute + category-attributes and sub-categoryAttributes
     * 
     * @param prodAttr
     * @param catAttr
     * @param subCatAttr
     * @return
     */
    private List<List<ExcelCell>> createRows(String version, ExcelSheet prodAttr, ExcelSheet catAttr) {

        if (prodAttr != null && catAttr != null) {

            int fontHeight = 16;

            List<List<ExcelCell>> rows = new ArrayList<List<ExcelCell>>();
            List<ExcelCell> row1 = new ArrayList<ExcelCell>();
            List<ExcelCell> row2 = new ArrayList<ExcelCell>();
            List<ExcelCell> row3 = new ArrayList<ExcelCell>();

            if (!prodAttr.getRows().isEmpty()) {
                row1.add(createExcell("Root", true, fontHeight, true, false));
                row2.add(createExcell("", true, fontHeight, true, true));
                row3.add(createExcell("", true, fontHeight));
                List<ExcelCell> genericAttr = new ArrayList<ExcelCell>(prodAttr.getRows().get(0));
                List<ExcelCell> genericAttribute = updateHeaderExcelCell(genericAttr, 12);
                row1.addAll(genericAttribute);
                row2.addAll(generateRestrictionInfo(genericAttribute, true));
                row3.addAll(generateRestrictionInfo(genericAttribute, false));
            }

            if (!catAttr.getRows().isEmpty()) {
                row1.add(createExcell("Media", true, fontHeight, true, false));
                row2.add(createExcell(catAttr.getName(), true, fontHeight, true, true));
                row3.add(createExcell("", true, fontHeight));
                List<ExcelCell> catAttribute = updateHeaderExcelCell(catAttr.getRows().get(0), 12);
                row1.addAll(catAttribute);
                row2.addAll(generateRestrictionInfo(catAttribute, true));
                row3.addAll(generateRestrictionInfo(catAttribute, false));
            }

            rows.add(row1);
            rows.add(row2);
            rows.add(row3);
            addRows(rows, row3, 2/* excelConfig.getMaxInputRows() */);

            return rows;

        }

        return null;
    }

    private void addRows(List<List<ExcelCell>> rows, List<ExcelCell> row, int count) {

        if (rows != null && row != null && count > 0) {
            for (int i = 0; i < count; i++) {
                rows.add(row);
            }
        }

    }

    /**
     * It helps to derive restriction ie: Enum-values/ Boolean and also helps to setup information for a given attribute
     * 
     * @param attributes
     * @param isInfoRow
     * @return
     */
    private List<ExcelCell> generateRestrictionInfo(List<ExcelCell> attributes, boolean isInfoRow) {

        if (attributes != null && !attributes.isEmpty()) {

            List<ExcelCell> newCells = new ArrayList<ExcelCell>();

            for (ExcelCell cell : attributes) {

                if (cell.getRestriction() != null) {
                    ExcelCell newCell = new ExcelCell();
                    newCell.setMaxOccurs(cell.getMaxOccurs());
                    newCell.setReferenceText(cell.getDisplayText());

                    if ((cell.getRestriction().isEnum() && cell.getRestriction().getEnumValues() != null)
                            || (cell.getRestriction().getBaseType() != null
                                    && "boolean".equalsIgnoreCase(cell.getRestriction().getBaseType()))) {
                        newCell.setRestriction(cell.getRestriction());

                        if (!isInfoRow) { /* Add Enun for non-info row only */
                            newCell.setConsiderEnum(true);
                            if (XsdBaseType.BOOLEAN.equalsIgnoreCase(cell.getRestriction().getBaseType())) {
                                List<String> list = new ArrayList<String>();
                                list.add("Y");
                                list.add("N");
                                newCell.getRestriction().setEnumValues(list);
                            }
                        } else {
                            // This is second-row which provides information/Documentation
                            String text = "";
                            if (StringUtils.isNotBlank(cell.getDocumentation())) {
                                text = cell.getDocumentation();
                            }
                            text += " (" + ExcelConstant.ENUM_TEXT_INFO + ")";
                            newCell.setDisplayText(text);
                            updateExcelCell(newCell, null, IndexedColors.BLACK, 16, false, true);
                        }

                    } else {
                        // This is second-row which provides information/Documentation
                        if (isInfoRow) {

                            String type = cell.getRestriction().getBaseType();
                            String text = null;
                            if (StringUtils.isNotBlank(cell.getDocumentation())) {
                                text = cell.getDocumentation();
                            } else {
                                text = ExcelConstant.STRING_TEXT_INFO;
                            }

                            if (XsdBaseType.DATE.equalsIgnoreCase(type)
                                    || XsdBaseType.DATE_TIME.equalsIgnoreCase(type)) {
                                text = "Enter the date (DD-MM-YYYY)";
                            } else if (ExcelFileHelper.isNumber(type)) {
                                text += " (" + (cell.isMultiSelect() ? ExcelConstant.MULTI_SELECT + " " : "")
                                        + ExcelConstant.STRING_VALUE_INFO + ")";
                            } else {
                                if (cell.getRestriction().getCharacterLength() > 0) {
                                    text += " (" + (cell.isMultiSelect() ? ExcelConstant.MULTI_SELECT + " " : "")
                                            + String.format(ExcelConstant.STRING_TEXT_INFO_WITH_CHAR_LENGTH,
                                                    cell.getRestriction().getCharacterLength())
                                            + ")";
                                }
                            }

                            newCell.setDisplayText(text);
                            updateExcelCell(newCell, null, IndexedColors.BLACK, 16, false, true);
                        } else {

                            String type = cell.getRestriction() != null ? cell.getRestriction().getBaseType() : null;

                            if (XsdBaseType.DATE.equalsIgnoreCase(type)
                                    || XsdBaseType.DATE_TIME.equalsIgnoreCase(type)) {
                            } else {
                                // This is third-row where client will provide input
                                if (ExcelFileHelper.isNumber(type)) {
                                    newCell.setNumberValidation(true);
                                }
                                newCell.setDisplayText(""); // TODO: CCM
                            }

                        }

                    }
                    newCells.add(newCell);
                } else {
                    newCells.add(createExcell("", false, -1));
                }
            }
            return newCells;
        }

        return null;
    }

    /**
     * It updates {@link ExcelCell} with required formatting.
     * 
     * @param list
     * @param backgroundColor
     * @return
     */
    private List<ExcelCell> updateExcelCell(List<ExcelCell> list, IndexedColors backgroundColor, int fontHeight) {

        if (list != null && !list.isEmpty()) {
            for (ExcelCell cell : list) {
                updateExcelCell(cell, backgroundColor, null, fontHeight, true, false);
            }
        }

        return list;
    }

    /**
     * It updates {@link ExcelCell} with required formatting.
     * 
     * @param list
     * @param backgroundColor
     * @return
     */
    private List<ExcelCell> updateHeaderExcelCell(List<ExcelCell> list, int fontHeight) {

        if (list != null && !list.isEmpty()) {
            for (ExcelCell cell : list) {
                IndexedColors backgroundColor = null;
                if (StringUtils.isNotBlank(cell.getDisplayText())) {
                    backgroundColor = cell.getDisplayText().endsWith("*") ? IndexedColors.LIGHT_GREEN
                            : IndexedColors.LIGHT_CORNFLOWER_BLUE;
                }
                updateExcelCell(cell, backgroundColor, null, fontHeight, true, false);
            }
        }

        return list;
    }

    private ExcelCell updateExcelCell(ExcelCell cell, IndexedColors backGroundColor, IndexedColors fontColor,
            int height, boolean isBold, boolean wrapText) {

        if (cell != null) {
            ExcelFormat format = new ExcelFormat();
            format.setBold(isBold);
            if (backGroundColor != null) {
                format.setBackgroundColor(backGroundColor);
            }
            if (fontColor != null) {
                format.setTextColor(fontColor);
            }
            format.setFontHeight((short) height);
            format.setWrapText(wrapText);
            format.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
            cell.setFormat(format);
        }

        return cell;
    }

    private ExcelCell createExcell(String text, boolean isBold, int fontHeight) {
        return createExcell(text, isBold, fontHeight, false, false);
    }

    /**
     * It creates new {@link ExcelCell}
     * 
     * @param text
     * @param isBold
     * @param fontHeight
     * @return
     */
    private ExcelCell createExcell(String text, boolean isBold, int fontHeight, boolean lockCell, boolean hideText) {

        ExcelCell cell = new ExcelCell();
        cell.setDisplayText(text);
        ExcelFormat format = new ExcelFormat();
        format.setBold(isBold);
        format.setAlignment(CellStyle.ALIGN_CENTER);
        format.setLockCell(lockCell);
        format.setHideText(hideText);
        if (fontHeight > 0) {
            format.setFontHeight((short) fontHeight);
        }
        cell.setFormat(format);
        return cell;
    }

    /**
     * It helps to create Index on given "ExcelCell Key: Excel-FileName + Sheet-Name + Cell-Name" Based on the text
     * defined into Excel-Cell: It returns Xpath of the Node which is definied into XSD
     * 
     * @param version
     * @param workBookName
     * @param excelSheet
     */
    private void buildCellIndex(String version, String workBookName, ExcelSheet excelSheet) {

        String method = "buildCellIndex";

        if (excelSheet != null && excelSheet.getRows() != null && !excelSheet.getRows().isEmpty()) {

            if (StringUtils.isNotBlank(workBookName) && StringUtils.isNotBlank(excelSheet.getName())) {

                for (List<ExcelCell> row : excelSheet.getRows()) {

                    if (row != null && !row.isEmpty()) {

                        for (ExcelCell cell : row) {

                            if (StringUtils.isNotBlank(cell.getDisplayText())) {
                                ExcelCellKey key = new ExcelCellKey(workBookName, excelSheet.getName(),
                                        cell.getDisplayText());
                                if (CELL_INDEX.get(version) == null) {
                                    CELL_INDEX.put(version, new HashMap<ExcelCellKey, ExcelCell>());
                                }
                                if (cell.getxPath() != null && CELL_INDEX.get(key) != null) {
                                    String msg = key + "is already present into this sheet:";
                                    // throw new IllegalArgumentException(msg);
                                    // log.error(msg);
                                }
                                CELL_INDEX.get(version).put(key, cell);
                            } else {
                                String msg = "display text is null/empty";
                                // log.error(msg);
                            }
                        }
                    }
                }
            } else {
                String msg = "workbok/sheename is null: " + workBookName + "," + excelSheet.getName();
                // log.error(msg);
            }
        } else {
            String msg = "excelSheet with emtpy row: excelSheet" + excelSheet;
            // log.error(msg);
        }
    }

}
