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

/**
 * 
 * It creates link from root-parent node to child node. It helps to find out parent or child of any current node.
 * 
 * @author rdhabal
 *
 */
public class Link {

    private Link parent; /* this links to parent of this node */
    private String myName;
    private Link child;
    private LinkType type;

    public Link(Link parent, String myName) {
        this.parent = parent;
        this.myName = myName;
        if (parent != null)
            this.parent.child = this;
    }

    public Link(Link parent, String myName, LinkType type) {
        this.parent = parent;
        this.myName = myName;
        this.type = type;
        if (parent != null)
            this.parent.child = this;
    }

    public Link(Link parent, String myName, XsdNodeType type) {
        this(parent, myName, LinkType.SIMPLE_TYPE);
        if (XsdNodeType.COMPLEX.equals(type)) {
            this.type = LinkType.COMPLEX_TYPE;
        }
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public Link getParent() {
        return parent;
    }

    public Link getChild() {
        return child;
    }

    public void setChild(Link child) {
        this.child = child;
    }

    public void setParent(Link parent) {
        this.parent = parent;
    }

    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    public Link getRootParent() {

        Link link = this;
        while (link.parent != null) {
            link = link.parent;
        }
        return link;
    }

    public String getXpath() {

        String path = this.getMyName();
        Link temp = this.parent;
        while (temp != null) {
            path = temp.getMyName() + "/" + path;
            temp = temp.parent;
        }

        return path;
    }

    @Override
    public String toString() {
        return "Link [myName=" + myName + "]";
    }

    public enum LinkType {

        SUBCATEGORY("SUBCATEGORY"), COMPLEX_TYPE("COMPLEX_TYPE"), SIMPLE_TYPE("SIMPLE_TYPE");

        private final String value;

        LinkType(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static LinkType fromValue(String v) {
            for (LinkType c : LinkType.values()) {

                if (c.value.equals(v)) {
                    return c;
                }
            }
            return null;
        }

    }

}
