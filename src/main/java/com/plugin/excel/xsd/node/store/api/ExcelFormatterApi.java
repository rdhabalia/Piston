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

package com.plugin.excel.xsd.node.store.api;

import com.plugin.excel.types.ExcelCell;
import com.plugin.excel.types.ExcelSheet;

/**
 * 
 * 
 * @author rdhabal
 *
 */
public interface ExcelFormatterApi {

    /**
     * It adds add cell into given ExcelSheet for given workBook file.
     * 
     * @param workBookName
     * @param excelSheet
     */
    void buildIndex(String version, String workBookName, ExcelSheet excelSheet);

    /**
     * It generates Excel sheet for all "Product" categories
     * 
     * @param version
     * @param root
     * @param directory
     */
    void generateProductExcel(String version, String root, String directory);

    /**
     * it takes cell-name defined into Excel sheet and returns its Node/Xpath which definied into XSD.
     * 
     * @param version
     * @param category
     * @param subCategory
     * @param cellName
     * @return
     */
    ExcelCell identifyCell(String version, String category, String subCategory, String cellName);

}
