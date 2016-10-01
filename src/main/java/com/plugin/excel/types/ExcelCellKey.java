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
 * It creates unique key to identify given Excel cell
 * 
 * @author rdhabal
 *
 */
public class ExcelCellKey {

    private String workBookName;
    private String excelSheetName;
    private String cellText;

    public ExcelCellKey(String workBookName, String excelSheetName, String cellText) {
        super();
        this.workBookName = workBookName;
        this.excelSheetName = excelSheetName;
        this.cellText = cellText;
    }

    public String getWorkBookName() {
        return workBookName;
    }

    public void setWorkBookName(String workBookName) {
        this.workBookName = workBookName;
    }

    public String getExcelSheetName() {
        return excelSheetName;
    }

    public void setExcelSheetName(String excelSheetName) {
        this.excelSheetName = excelSheetName;
    }

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cellText == null) ? 0 : cellText.hashCode());
        result = prime * result + ((excelSheetName == null) ? 0 : excelSheetName.hashCode());
        result = prime * result + ((workBookName == null) ? 0 : workBookName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExcelCellKey other = (ExcelCellKey) obj;
        if (cellText == null) {
            if (other.cellText != null)
                return false;
        } else if (!cellText.equals(other.cellText))
            return false;
        if (excelSheetName == null) {
            if (other.excelSheetName != null)
                return false;
        } else if (!excelSheetName.equals(other.excelSheetName))
            return false;
        if (workBookName == null) {
            if (other.workBookName != null)
                return false;
        } else if (!workBookName.equals(other.workBookName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ExcelCellKey [workBookName=" + workBookName + ", excelSheetName=" + excelSheetName + ", cellText="
                + cellText + "]";
    }

}
