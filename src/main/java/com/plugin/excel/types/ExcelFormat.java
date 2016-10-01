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

import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * It requires to format Excel cell format
 * 
 * @author rdhabal
 *
 */
public class ExcelFormat {

    private String font;
    private boolean bold;
    private IndexedColors backgroundColor;
    private IndexedColors textColor;
    private short alignment;
    private short fontHeight;
    private boolean wrapText;
    private int cellHeight;
    private boolean lockCell;
    private boolean hideText;
    private boolean date;

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public boolean isHideText() {
        return hideText;
    }

    public void setHideText(boolean hideText) {
        if (hideText) {
            backgroundColor = IndexedColors.WHITE;
        }
        this.hideText = hideText;
    }

    public boolean isLockCell() {
        return lockCell;
    }

    public void setLockCell(boolean lockCell) {
        this.lockCell = lockCell;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public short getAlignment() {
        return alignment;
    }

    public void setAlignment(short alignment) {
        this.alignment = alignment;
    }

    public IndexedColors getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(IndexedColors backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public IndexedColors getTextColor() {
        return textColor;
    }

    public void setTextColor(IndexedColors textColor) {
        this.textColor = textColor;
    }

    public short getFontHeight() {
        return fontHeight;
    }

    public void setFontHeight(short fontHeight) {
        this.fontHeight = fontHeight;
    }

    public boolean isWrapText() {
        return wrapText;
    }

    public void setWrapText(boolean wrapText) {
        this.wrapText = wrapText;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

}
