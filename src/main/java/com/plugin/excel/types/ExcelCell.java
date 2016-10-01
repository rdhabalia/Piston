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

import org.apache.commons.lang3.StringUtils;

import com.plugin.excel.type.node.Link;
import com.plugin.excel.type.node.Restriction;

/**
 * It stores excel row-cell
 * 
 * @author rdhabal
 *
 */
public class ExcelCell implements Cloneable {

    private String displayText;
    private String xmlName;
    private boolean required;
    private Restriction restriction;
    private Link xPath;
    private ExcelFormat format;
    private boolean considerEnum;
    private String documentation;
    private String referenceText;
    private boolean numberValidation;
    private String minOccurs;
    private String maxOccurs;

    public String getReferenceText() {
        return referenceText;
    }

    public void setReferenceText(String referenceText) {
        this.referenceText = referenceText;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Link getxPath() {
        return xPath;
    }

    public void setxPath(Link xPath) {
        this.xPath = xPath;
    }

    public String getXmlName() {
        return xmlName;
    }

    public void setXmlName(String xmlName) {
        this.xmlName = xmlName;
    }

    public ExcelFormat getFormat() {
        return format;
    }

    public void setFormat(ExcelFormat format) {
        this.format = format;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isConsiderEnum() {
        return considerEnum;
    }

    public void setConsiderEnum(boolean considerEnum) {
        this.considerEnum = considerEnum;
    }

    public ExcelCell clone() {
        try {
            return (ExcelCell) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDocumentation() {
        if (StringUtils.isNotBlank(documentation)) {
            StringBuilder sb = new StringBuilder(documentation);
            int i = 0;
            int length = 70;
            while (i + length < sb.length() && (i = sb.lastIndexOf(" ", i + length)) != -1) {
                sb.replace(i, i + 1, "\n");
            }
            return sb.toString();
        }
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    @Override
    public String toString() {
        return "ExcelCell [displayText=" + displayText + ", xmlName=" + xmlName + ", required=" + required + ", xPath="
                + xPath + ", considerEnum=" + considerEnum + "]";
    }

    public boolean isNumberValidation() {
        return numberValidation;
    }

    public void setNumberValidation(boolean numberValidation) {
        this.numberValidation = numberValidation;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public boolean isMultiSelect() {
        if (StringUtils.isNotBlank(this.maxOccurs)) {
            return maxOccurs.equalsIgnoreCase("unbounded") || maxOccurs.equalsIgnoreCase("-1");
        }
        return false;
    }

}
