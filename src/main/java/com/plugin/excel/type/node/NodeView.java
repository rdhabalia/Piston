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

package com.plugin.excel.type.node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * It creates a snapshot view of Node so, parent node can contains child node's view to maintain relationship between
 * them.
 * 
 * @author rdhabal
 *
 */
public class NodeView implements Serializable {

    private String type; /* primary key */
    private String nodeTagName;
    private String complexNodeTypeName;
    private String minOccurs;
    private String maxOccurs;
    private Map<ViewInfoToken, List<String>> additionalViewInformation = new HashMap<ViewInfoToken, List<String>>();
    private String nodeId;
    private String simpleNodeTypeName;
    private String mpRequirementLevel;
    private String supplierRequirementLevel;
    private Map<String, String> mpConditionalAttributes;
    private Map<String, String> supplierConditionalAttributes;
    private boolean ignoreMpXsd;
    private boolean ignoreSupplierXsd;

    public NodeView() {
    }

    public NodeView(String nodeTagName, String type, String nodeId, String minOccurs, String maxOccurs) {
        super();
        this.type = type;
        this.nodeTagName = nodeTagName;
        this.minOccurs = minOccurs;
        this.maxOccurs = maxOccurs;
        this.nodeId = nodeId;
    }

    public String getNodeTagName() {
        return nodeTagName;
    }

    public void setNodeTagName(String nodeTagName) {
        this.nodeTagName = nodeTagName;
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

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
        if (node != null) {/* if node is not null -> view is loaded with node */
            setLoaded(true);
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    private Node node;
    private boolean isLoaded = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<ViewInfoToken, List<String>> getAdditionalViewInformation() {
        return additionalViewInformation;
    }

    public void setAdditionalViewInformation(Map<ViewInfoToken, List<String>> additionalViewInformation) {
        this.additionalViewInformation = additionalViewInformation;
    }

    public String getComplexNodeTypeName() {
        return complexNodeTypeName;
    }

    public void setComplexNodeTypeName(String complexNodeTypeName) {
        this.complexNodeTypeName = complexNodeTypeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public String toString() {
        return "NodeView [type=" + type + ", nodeTagName=" + nodeTagName + ", complexNodeTypeName="
                + complexNodeTypeName + ", minOccurs=" + minOccurs + ", maxOccurs=" + maxOccurs + ", nodeId=" + nodeId
                + ", isLoaded=" + isLoaded + "]";
    }

    public String getSimpleNodeTypeName() {
        return simpleNodeTypeName;
    }

    public void setSimpleNodeTypeName(String simpleNodeTypeName) {
        this.simpleNodeTypeName = simpleNodeTypeName;
    }

    public String getMpRequirementLevel() {
        return mpRequirementLevel;
    }

    public void setMpRequirementLevel(String mpRequirementLevel) {
        this.mpRequirementLevel = mpRequirementLevel;
    }

    public boolean isIgnoreMpXsd() {
        return ignoreMpXsd;
    }

    public void setIgnoreMpXsd(boolean ignoreMpXsd) {
        this.ignoreMpXsd = ignoreMpXsd;
    }

    public boolean isIgnoreSupplierXsd() {
        return ignoreSupplierXsd;
    }

    public void setIgnoreSupplierXsd(boolean ignoreSupplierXsd) {
        this.ignoreSupplierXsd = ignoreSupplierXsd;
    }

    public String getSupplierRequirementLevel() {
        return supplierRequirementLevel;
    }

    public void setSupplierRequirementLevel(String supplierRequirementLevel) {
        this.supplierRequirementLevel = supplierRequirementLevel;
    }

    public Map<String, String> getMpConditionalAttributes() {
        return mpConditionalAttributes;
    }

    public void setMpConditionalAttributes(Map<String, String> mpConditionalAttributes) {
        this.mpConditionalAttributes = mpConditionalAttributes;
    }

    public Map<String, String> getSupplierConditionalAttributes() {
        return supplierConditionalAttributes;
    }

    public void setSupplierConditionalAttributes(Map<String, String> supplierConditionalAttributes) {
        this.supplierConditionalAttributes = supplierConditionalAttributes;
    }

}
