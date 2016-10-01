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

/**
 * Node data-structure to store XSD node and its children information.
 * 
 * @author rdhabal
 *
 */
public class Node implements Serializable {

    private String nodeName; /* primary key */
    private XsdNodeType nodeType;
    private GroupNode group;
    private Restriction restriction;
    private String doucument;
    private String version;
    private MetadataType metaType;
    private boolean isLocalizable;
    private String complexNodeTypeName;
    private String id;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public XsdNodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(XsdNodeType nodeType) {
        this.nodeType = nodeType;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public String getDoucument() {
        return doucument;
    }

    public void setDoucument(String doucument) {
        this.doucument = doucument;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public MetadataType getMetaType() {
        return metaType;
    }

    public void setMetaType(MetadataType metaType) {
        this.metaType = metaType;
    }

    public GroupNode getGroup() {
        return group;
    }

    public void setGroup(GroupNode group) {
        this.group = group;
    }

    public boolean getIsLocalizable() {
        return isLocalizable;
    }

    public void setIsLocalizable(boolean isLocalizable) {
        this.isLocalizable = isLocalizable;
    }

    public String getComplexNodeTypeName() {
        return complexNodeTypeName;
    }

    public void setComplexNodeTypeName(String complexNodeTypeName) {
        this.complexNodeTypeName = complexNodeTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node [nodeName=" + nodeName + ", nodeType=" + nodeType + ", group=" + group + ", restriction="
                + restriction + ", version=" + version + ", metaType=" + metaType + ", complexNodeTypeName="
                + complexNodeTypeName + ", id=" + id + "]";
    }

}
