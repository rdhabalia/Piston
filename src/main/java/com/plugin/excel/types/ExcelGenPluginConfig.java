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

/**
 * 
 * @author rdhabalia
 *
 */
public class ExcelGenPluginConfig {

    private int headerRowHeight;
    private int documentRowHeight;
    private int documentFont;

    public int getDocumentFont() {
        return documentFont;
    }

    public void setDocumentFont(int documentFont) {
        this.documentFont = documentFont;
    }

    public int getHeaderRowHeight() {
        return headerRowHeight;
    }

    public void setHeaderRowHeight(int headerRowHeight) {
        this.headerRowHeight = headerRowHeight;
    }

    public int getDocumentRowHeight() {
        return documentRowHeight;
    }

    public void setDocumentRowHeight(int documentRowHeight) {
        this.documentRowHeight = documentRowHeight;
    }

    public static ExcelGenPluginConfig config() {
        ExcelGenPluginConfig config = new ExcelGenPluginConfig();
        config.setHeaderRowHeight(500);
        config.setDocumentRowHeight(500);
        config.setDocumentFont(16);
        return config;
    }

}
