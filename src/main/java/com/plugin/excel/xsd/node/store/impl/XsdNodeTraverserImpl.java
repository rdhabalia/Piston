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

package com.plugin.excel.xsd.node.store.impl;

import org.apache.commons.lang3.StringUtils;

import com.plugin.excel.type.node.GroupNode;
import com.plugin.excel.type.node.Node;
import com.plugin.excel.type.node.NodeView;
import com.plugin.excel.xsd.node.store.api.XsdNodeTraverser;

/**
 * 
 * It traverse given complex {@Node}
 * 
 * @author rdhabal
 *
 */
public class XsdNodeTraverserImpl implements XsdNodeTraverser {

    public void traverse(Node node) {

        if (node != null) {
            System.out.println(node.getNodeName());
            traverse(node.getGroup());
        }

    }

    private void traverse(GroupNode group) {

        if (group != null) {

            System.out.println("**********Start: " + group.getType().value() + "**********");
            if (group.getViews() != null && !group.getViews().isEmpty()) {
                for (NodeView view : group.getViews()) {
                    System.out.print("name: " + view.getNodeTagName());
                    if (StringUtils.isNotBlank(view.getComplexNodeTypeName())) {
                        System.out.print(", ComplexType: " + view.getComplexNodeTypeName());
                    }
                    if (StringUtils.isNotBlank(view.getSimpleNodeTypeName())) {
                        System.out.print(", SimpleType: " + view.getSimpleNodeTypeName());
                    }
                    if (StringUtils.isNotBlank(view.getMinOccurs())) {
                        System.out.print(", min: " + view.getMinOccurs());
                    }
                    if (StringUtils.isNotBlank(view.getMaxOccurs())) {
                        System.out.print(", max: " + view.getMaxOccurs());
                    }
                    if (view.getNode() != null && view.getNode().getRestriction() != null) {
                        System.out.print(", baseType: " + view.getNode().getRestriction().getBaseType());
                        System.out.print(", charLength: " + view.getNode().getRestriction().getCharacterLength());
                        System.out.print(", EnumValues: " + view.getNode().getRestriction().getEnumValues());
                    }
                    if (view.getNode() != null && view.getNode().getGroup() != null) {
                        traverse(view.getNode().getGroup());
                    }
                    System.out.println("end of name: " + view.getNodeTagName());
                }
            }

            if (group.getGroups() != null && !group.getGroups().isEmpty()) {
                for (GroupNode grp : group.getGroups()) {
                    traverse(grp);
                }

            }

            System.out.println("**********End: " + group.getType().value() + "**********");
        }

    }

}
