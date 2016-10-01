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
import java.util.ArrayList;
import java.util.List;

/**
 * It combines NodeView for a given {@link Node}
 * 
 * @author rdhabal
 *
 */
public class GroupNode implements Serializable {

    private GroupType type;
    private List<NodeView> views = new ArrayList<NodeView>();
    private List<GroupNode> groups = new ArrayList<GroupNode>();
    private String minOccurs;
    private String maxOccurs;

    public GroupNode(GroupType type) {
        this.type = type;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }

    public List<NodeView> getViews() {
        return views;
    }

    public void setViews(List<NodeView> views) {
        this.views = views;
    }

    public List<GroupNode> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupNode> groups) {
        this.groups = groups;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

}
