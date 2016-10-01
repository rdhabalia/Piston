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

package com.plugin.excel.types;

import java.util.HashMap;
import java.util.Map;

/**
 * It represents One excel work book for Product categories
 * 
 * @author rdhabal
 *
 */
public class ProductWorkBook {

    private String categoryName;
    private Map<String, ExcelSheet> subCategorySheets = new HashMap<String, ExcelSheet>();
    private ExcelSheet caetgoryAttributes;
    private ExcelSheet productAttributes;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ExcelSheet getCaetgoryAttributes() {
        return caetgoryAttributes;
    }

    public void setCaetgoryAttributes(ExcelSheet caetgoryAttributes) {
        this.caetgoryAttributes = caetgoryAttributes;
    }

    public ExcelSheet getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(ExcelSheet productAttributes) {
        this.productAttributes = productAttributes;
    }

    public Map<String, ExcelSheet> getSubCategorySheets() {
        return subCategorySheets;
    }

    public void setSubCategorySheets(Map<String, ExcelSheet> subCategorySheets) {
        this.subCategorySheets = subCategorySheets;
    }

}
