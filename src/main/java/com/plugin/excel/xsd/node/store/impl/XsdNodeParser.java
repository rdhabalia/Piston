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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.impl.Const;
import com.sun.xml.xsom.parser.XSOMParser;
import com.plugin.excel.type.node.GroupNode;
import com.plugin.excel.type.node.GroupType;
import com.plugin.excel.type.node.MetadataType;
import com.plugin.excel.type.node.Node;
import com.plugin.excel.type.node.NodeView;
import com.plugin.excel.type.node.Restriction;
import com.plugin.excel.type.node.XsdNodeType;
import com.plugin.excel.xsd.parse.AnnotationFactory;

/**
 * It parses given XSD and prepares Metadata out of the given list of XSDs.
 * 
 * @author rdhabal
 *
 */
public class XsdNodeParser {

    private XSSchemaSet schemaSet;
    private Map<String, Node> simpleNodes = new HashMap<String, Node>();
    private Map<String, Node> complexNodes = new HashMap<String, Node>();

    /**
     * It receives list of XSD files and ask Parsers to parse them.
     * 
     * @param xsds
     */
    public XsdNodeParser(List<String> xsds) {

        XSOMParser parser = new XSOMParser();
        try {
            for (String xsd : xsds) {
                parser.setAnnotationParser(new AnnotationFactory());
                parser.parse(new File(xsd));
            }
            schemaSet = parser.getResult();
        } catch (SAXException e) {
            // TODO: log and throw exception
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            // TODO: log and throw exception
            throw new IllegalArgumentException(e);
        }
    }

    public XsdNodeParser(File parentDirectory, String ignoreDirectories) {

        XSOMParser parser = new XSOMParser();
        try {

            List<File> files = new ArrayList<File>();
            FileHelper.findFiles(parentDirectory, files);
            for (File xsd : files) {
                if (xsd.getName().endsWith(".xsd") && !isInIgnoreDirectory(xsd.getPath(), ignoreDirectories)) {
                    parser.setAnnotationParser(new AnnotationFactory());
                    parser.parse(xsd);
                }
            }
            schemaSet = parser.getResult();
        } catch (SAXException e) {
            // TODO: log and throw exception
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            // TODO: log and throw exception
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * It returns all SimpleNodes which are defined into provided list of XSDs.
     * 
     * @return
     */
    public Map<String, Node> getSimpleNodes() {

        if (!simpleNodes.isEmpty()) {
            return simpleNodes;
        }

        if (schemaSet != null && !schemaSet.getSchemas().isEmpty()) {

            for (XSSchema schema : schemaSet.getSchemas()) {
                parseSimpleNodes(schema);
            }
        }

        return simpleNodes;
    }

    /**
     * It returns all ComplexNodes which are defined into provided list of XSDs.
     * 
     * @return
     */
    public Map<String, Node> getComplexNodes() {

        if (!complexNodes.isEmpty()) {
            return complexNodes;
        }

        if (schemaSet != null && !schemaSet.getSchemas().isEmpty()) {

            for (XSSchema schema : schemaSet.getSchemas()) {
                parseComplexNodes(schema);
            }
        }

        return complexNodes;
    }

    /**
     * It builds {@link Node}-Tree of given Complex-Type into Hierarchical Tree structure.
     * 
     * @param nodeName
     * @return
     */
    public Node buildComplexNode(String nodeName) {

        initTypes();
        if (complexNodes.get(nodeName) != null) {
            Node node = complexNodes.get(nodeName);
            loadChildren(node.getGroup());
            return node;
        }

        return null;
    }

    /**
     * It loads all children comes under given Complex-Node.
     * 
     * @param group
     */
    private void loadChildren(GroupNode group) {

        if (group != null) {
            List<NodeView> views = group.getViews();
            if (views != null && !views.isEmpty()) {
                for (NodeView view : views) {
                    Node node = null;
                    if (!view.isLoaded() && StringUtils.isNotBlank(view.getComplexNodeTypeName())) {
                        node = complexNodes.get(view.getComplexNodeTypeName());
                        if (node != null) {
                            loadChildren(node.getGroup());
                        } else {
                            // TODO: log and throw exception
                            throw new IllegalStateException("ComplexType not present:" + view.getComplexNodeTypeName());
                        }
                    } else if (!view.isLoaded() && StringUtils.isNotBlank(view.getSimpleNodeTypeName())) {
                        node = simpleNodes.get(view.getSimpleNodeTypeName());
                        if (node == null) {
                            // TODO: log and throw exception
                            throw new IllegalStateException("Simple not present:" + view.getSimpleNodeTypeName());
                        }
                    }
                    if (node != null) {
                        // if(!StringUtils.isNotBlank(node.getDoucument()) &&
                        // StringUtils.isNotBlank(view.getType())){node.setDoucument(view.getType());/*It's a hack as
                        // sometime documentation is defined at view level*/}
                        view.setNode(node);
                    }
                }
            }

            if (group.getGroups() != null && !group.getGroups().isEmpty()) {
                for (GroupNode grp : group.getGroups()) {
                    loadChildren(grp);
                }
            }
        }

    }

    /**
     * It prepares collection of SimpleNode and ComplexNode present into list of provided XSDs.
     * 
     */
    private void initTypes() {
        getSimpleNodes();
        getComplexNodes();
    }

    /**
     * It parses complexnodes which are present into given Schema
     * 
     * @param schema
     */
    private void parseComplexNodes(XSSchema schema) {
        if (Const.schemaNamespace.equals(schema.getTargetNamespace())) {
            return;
        }
        for (XSComplexType complexType : schema.getComplexTypes().values()) {
            Node node = getComplexType(complexType);
            if (node != null) {
                complexNodes.put(node.getNodeName(), node);
            }
        }
    }

    /**
     * It parses simpleNodes which are present into given Schema
     * 
     * @param
     */
    public void parseSimpleNodes(XSSchema s) {
        if (s.getTargetNamespace().equals(Const.schemaNamespace)) {
            return;
        }
        for (XSSimpleType simpleType : s.getSimpleTypes().values()) {
            Node node = getSimpleType(simpleType);
            if (node != null) {
                simpleNodes.put(node.getNodeName(), node);
            }
        }
    }

    /************************************************* helper methods ********************************************/

    /**
     * It creates Complex {@Node} structure from XSD complex-element
     * 
     * @param complexType
     * @return
     */
    private Node getComplexType(XSComplexType complexType) {

        Node node = new Node();

        node.setNodeName(complexType.getName());
        if (complexType.getAnnotation() != null && complexType.getAnnotation().getAnnotation() != null) {
            node.setDoucument(complexType.getAnnotation().getAnnotation().toString());
        }

        XSTerm pterm = complexType.getContentType().asParticle().getTerm();

        if (pterm.isModelGroup()) {

            XSModelGroup xsModelGroup = pterm.asModelGroup();
            XSParticle[] particles = xsModelGroup.getChildren();

            if (XSModelGroup.SEQUENCE == xsModelGroup.getCompositor()) {

                GroupNode group = null;

                if (node.getGroup() == null) {
                    group = new GroupNode(GroupType.SEQUENCE);
                    node.setGroup(group);
                } else {
                    group = new GroupNode(GroupType.SEQUENCE);
                    node.getGroup().setGroups(new ArrayList<GroupNode>());
                    node.getGroup().getGroups().add(group);
                }
                buildComplexNode(particles, group);
            }

            for (XSParticle p2 : particles) {

                GroupNode group = null;

                if (node.getGroup() == null) {
                    group = new GroupNode(GroupType.CHOICE);
                    node.setGroup(group);
                } else {
                    group = new GroupNode(GroupType.CHOICE);
                    node.getGroup().setGroups(new ArrayList<GroupNode>());
                    node.getGroup().getGroups().add(group);
                }

                addChoiceElements(p2, group);
            }

        }

        return node;

    }

    /**
     * If XSD Complex-Element has "Choice" under it then it updates given {@Node} with this information
     * 
     * @param xsParticle
     * @param group
     */
    private void addChoiceElements(XSParticle xsParticle, GroupNode group) {

        if (xsParticle != null) {

            XSTerm pterm = xsParticle.getTerm();
            if (pterm.isModelGroup()) {

                XSModelGroup xsModelGroup = pterm.asModelGroup();
                XSParticle[] particles = xsModelGroup.getChildren();

                if (XSModelGroup.CHOICE == xsModelGroup.getCompositor()) {
                    buildComplexNode(particles, group);
                }

            }
        }
    }

    /**
     * from xsParticleArray: it prepares {@GroupNode} under given Complex {@Node}
     * 
     * @param xsParticleArray
     * @param group
     */
    private void buildComplexNode(XSParticle[] xsParticleArray, GroupNode group) {

        if (xsParticleArray != null && group != null) {

            if (group.getViews() == null) {
                group.setViews(new ArrayList<NodeView>());
            }

            for (XSParticle xsParticle : xsParticleArray) {
                if (xsParticle != null) {
                    XSTerm pterm = xsParticle.getTerm();
                    if (pterm.isElementDecl()) {

                        NodeView view = new NodeView();
                        view.setMinOccurs("" + xsParticle.getMinOccurs());
                        view.setMaxOccurs("" + xsParticle.getMaxOccurs());

                        if (pterm.asElementDecl().getType().asComplexType() != null) {

                            view.setComplexNodeTypeName(pterm.asElementDecl().getType().getName());
                            view.setNodeTagName(pterm.asElementDecl().getName());
                            if (pterm.asElementDecl().getAnnotation() != null
                                    && pterm.asElementDecl().getAnnotation().getAnnotation() != null) {
                                view.setType(pterm.asElementDecl().getAnnotation().getAnnotation().toString());
                            }
                            /*
                             * Node child = createNode(pterm.asElementDecl().getName(),XsdNodeType.COMPLEX);
                             * child.setComplexNodeTypeName(pterm.asElementDecl().getType().getName());
                             */

                        } else {

                            if (pterm.asElementDecl().getType().asSimpleType() != null
                                    && pterm.asElementDecl().getType().asSimpleType().getName() != null) {
                                Node child = createNode(pterm.asElementDecl().getName(), XsdNodeType.SIMPLE);
                                view.setSimpleNodeTypeName(pterm.asElementDecl().getType().getName());
                                view.setNodeTagName(pterm.asElementDecl().getName());
                                Node simpleNode = simpleNodes.get(view.getSimpleNodeTypeName());
                                if (simpleNode != null) {
                                    child.setRestriction(
                                            simpleNodes.get(view.getSimpleNodeTypeName()).getRestriction());
                                } else {
                                    // dateTime can't be detected as base-type and comes here
                                    if (StringUtils.isNotBlank(view.getSimpleNodeTypeName())) {
                                        Restriction restriction = new Restriction();
                                        restriction.setBaseType(view.getSimpleNodeTypeName());
                                        child.setRestriction(restriction);
                                    }
                                }
                                if (pterm.asElementDecl().getAnnotation() != null
                                        && pterm.asElementDecl().getAnnotation().getAnnotation() != null) {
                                    child.setDoucument(
                                            pterm.asElementDecl().getAnnotation().getAnnotation().toString());
                                }

                                view.setNode(child);
                            } else {
                                Node child = createNode(pterm.asElementDecl().getName(), XsdNodeType.SIMPLE);
                                Restriction restriction = new Restriction();
                                restriction.setBaseType(pterm.asElementDecl().getType().getBaseType().getName());
                                if (pterm.asElementDecl().getType() != null
                                        && pterm.asElementDecl().getType().asSimpleType() != null
                                        && pterm.asElementDecl().getType().asSimpleType().asRestriction() != null) {
                                    List<XSFacet> enums = pterm.asElementDecl().getType().asSimpleType().asRestriction()
                                            .getDeclaredFacets("enumeration");
                                    if (enums != null && !enums.isEmpty()) {
                                        List<String> enumVals = new ArrayList<String>();
                                        for (XSFacet enumVal : enums) {
                                            enumVals.add(enumVal.getValue().value);
                                        }
                                        restriction.setEnum(true);
                                        restriction.setEnumValues(enumVals);
                                    }
                                }
                                String maxLength = getMaxLength(pterm);
                                if (StringUtils.isNotBlank(maxLength)) {
                                    restriction.setCharacterLength(Integer.parseInt(maxLength));
                                }
                                child.setRestriction(restriction);
                                view.setNodeTagName(child.getNodeName());
                                XSSimpleType simpleElement = pterm.asElementDecl().getType().asSimpleType();
                                // add documentation
                                if (pterm.asElementDecl().getAnnotation() != null
                                        && pterm.asElementDecl().getAnnotation().getAnnotation() != null) {
                                    child.setDoucument(
                                            pterm.asElementDecl().getAnnotation().getAnnotation().toString());
                                }
                                view.setNode(child);

                            }

                        }

                        group.getViews().add(view);
                    }
                }
            }

        }

    }

    /**
     * It derives MaxLength of element if it has baseType = String and XSD has MaxLength information
     * 
     * @param pterm
     * @return
     */
    private static String getMaxLength(XSTerm pterm) {

        if (pterm != null && pterm.asElementDecl().getType() != null
                && pterm.asElementDecl().getType().asSimpleType() != null
                && pterm.asElementDecl().getType().asSimpleType().asRestriction() != null
                && pterm.asElementDecl().getType().asSimpleType().asRestriction().getDeclaredFacets() != null) {

            if (pterm.asElementDecl().getType().asSimpleType().asRestriction().getDeclaredFacets("maxLength") != null
                    && pterm.asElementDecl().getType().asSimpleType().asRestriction().getDeclaredFacets("maxLength")
                            .size() > 0) {
                return pterm.asElementDecl().getType().asSimpleType().asRestriction().getDeclaredFacets("maxLength")
                        .get(0).getValue().value;
            }

        }

        return null;
    }

    private static Node createNode(String name, XsdNodeType nodeType) {
        Node node = new Node();
        node.setNodeName(name);
        node.setNodeType(nodeType);
        return node;
    }

    /**
     * Create Node with SimpleType from the XSD-SimpleType element
     * 
     * @param simpleType
     * @return
     */
    private Node getSimpleType(XSSimpleType simpleType) {

        if (simpleType != null) {
            Node node = new Node();

            node.setNodeType(XsdNodeType.SIMPLE);
            node.setMetaType(MetadataType.OTHER);
            node.setNodeName(simpleType.getName());

            // Set enums of given SimpleType
            if (simpleType.asRestriction() != null
                    && simpleType.asRestriction().getDeclaredFacets("enumeration") != null) {

                Restriction restriction = new Restriction();
                restriction.setEnum(true);
                restriction.setEnumValues(new ArrayList<String>());
                for (XSFacet facet : simpleType.asRestriction().getDeclaredFacets("enumeration")) {
                    restriction.getEnumValues().add(facet.getValue().value);
                }
                restriction.setBaseType(simpleType.getBaseType().getName());
                // restriction.setCharacterLength(characterLength); //TODO:
                node.setRestriction(restriction);
            }

            return node;
        }
        return null;
    }

    private boolean isInIgnoreDirectory(String filePath, String ignoreDirectories) {

        if (StringUtils.isNotBlank(filePath) && StringUtils.isNotBlank(ignoreDirectories)) {
            String[] dirs = ignoreDirectories.split(",");
            for (String dir : dirs) {
                if (filePath.contains(dir)) {
                    return true;
                }
            }

        }

        return false;
    }

}
