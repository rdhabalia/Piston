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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.plugin.excel.type.node.GroupNode;
import com.plugin.excel.type.node.GroupType;
import com.plugin.excel.type.node.Link;
import com.plugin.excel.type.node.Link.LinkType;
import com.plugin.excel.type.node.Node;
import com.plugin.excel.type.node.NodeView;
import com.plugin.excel.types.ExcelCell;
import com.plugin.excel.types.ExcelSheet;
import com.plugin.excel.types.ProductWorkBook;

/**
 * It manages root related metadata node management.
 * 
 * @author rdhabal
 *
 */
public class RootNodeManager {

    @Autowired
    private XsdNodeParserManagerImpl xsdParserMgr;

    /**
     * It accepts category ie: Movie, Album etc. and returns entire ExcelSheet.
     * 
     * @param categoryName
     * @return
     */
    public ProductWorkBook buildExcelSheet(String version, String categoryName, Link root) {

        Node node = xsdParserMgr.buildComplexNode(version, categoryName);

        if (node != null) {

            ProductWorkBook book = new ProductWorkBook();
            Link category = new Link(root, categoryName, LinkType.SUBCATEGORY);
            ExcelSheet caetgoryAttributes = getAttributeList(version, category, categoryName);
            book.setCategoryName(categoryName);
            book.setCaetgoryAttributes(caetgoryAttributes);

            Map<String, NodeView> subCategories = getSubCategories(version, categoryName);
            if (subCategories != null && !subCategories.isEmpty()) {
                for (Entry<String, NodeView> entry : subCategories.entrySet()) {
                    String subCategoryName = entry.getValue().getNodeTagName();
                    Link subCategoryLink = new Link(category, subCategoryName, LinkType.SUBCATEGORY);
                    ExcelSheet attributes = getAttributeList(version, subCategoryLink,
                            entry.getValue().getComplexNodeTypeName());
                    book.getSubCategorySheets().put(subCategoryName, attributes);
                }
            }
            // add General tab which contains "Product-attribute" and "Category-attribute"
            String sheetName = "Other";
            book.getSubCategorySheets().put(sheetName, null);

            return book;
        }

        return null;
    }

    /**
     * It creates list of ExcelWorkBook for all the categories comes under "Product"
     * 
     * @param version
     * @param root
     * @return
     */
    public Map<String, ProductWorkBook> buildExcelWorkBook(String version, String root) {

        Node node = xsdParserMgr.buildComplexNode(version, root);

        if (node != null) {
            Map<String, ProductWorkBook> map = new HashMap<String, ProductWorkBook>();
            Link rootLink = new Link(null, root, LinkType.SUBCATEGORY);
            ExcelSheet rootAttribute = getAttributeList(version, rootLink, root);

            Map<String, NodeView> subCategories = getSubCategories(version, root);
            if (subCategories != null && !subCategories.isEmpty()) {
                for (Entry<String, NodeView> entry : subCategories.entrySet()) {
                    String subCategoryName = entry.getValue().getComplexNodeTypeName();
                    ProductWorkBook book = buildExcelSheet(version, subCategoryName, rootLink);
                    book.setProductAttributes(rootAttribute);
                    map.put(subCategoryName, book);
                }
            }
            return map;
        }
        return null;
    }

    /**
     * It returns all supported versions
     * 
     * @return
     */
    public Set<String> getSupportedVersions() {
        return xsdParserMgr.getSupportedVersions();
    }

    /**
     * It will create ExcelSheet which contains list of attributes which immediately comes under that Complex-Node
     * 
     * @param parent
     * @return
     */
    public ExcelSheet getAttributeList(String version, Link parent, String parentComplexName) {
        return getAttributeList(version, parent, parentComplexName, -1);
    }

    public ExcelSheet getAttributeList(String version, Link parent, String parentComplexName,
            int duplicateColumnNumber) {
        return getAttributeList(version, parent, parentComplexName, duplicateColumnNumber, false);
    }

    public ExcelSheet getAttributeList(String version, Link parent, String parentComplexName, int duplicateColumnNumber,
            boolean shouldParentConsider) {

        Node node = xsdParserMgr.buildComplexNode(version, parentComplexName);

        if (node != null && node.getGroup() != null && !node.getGroup().getViews().isEmpty()) {

            ExcelSheet sheet = new ExcelSheet();
            sheet.setName(parentComplexName);
            sheet.getRows().add(new ArrayList<ExcelCell>());

            for (NodeView view : node.getGroup().getViews()) {

                // NodeView treatView = SpecialTreatmentHandler.shouldTreat(view); /*We require if we want to give
                // special treatment*/
                List<ExcelCell> cells = createCell(view, node, parent, duplicateColumnNumber, shouldParentConsider);
                sheet.getRows().get(0).addAll(cells);
            }

            return sheet;

        }

        return null;
    }

    /**
     * It returns all sub-categories presences under the given category
     * 
     * @param category
     * @return
     */
    public Map<String, NodeView> getSubCategories(String version, String category) {

        Node node = xsdParserMgr.buildComplexNode(version, category);

        if (node != null && node.getGroup() != null && node.getGroup().getGroups() != null
                && !node.getGroup().getGroups().isEmpty()) {

            GroupNode subCategory = node.getGroup().getGroups().get(0);

            if (GroupType.CHOICE.equals(subCategory.getType())) {

                Map<String, NodeView> subCategories = new HashMap<String, NodeView>();

                for (NodeView view : subCategory.getViews()) {
                    subCategories.put(view.getComplexNodeTypeName(), view);
                }
                return subCategories;
            }
        }
        return null;
    }

    /**
     * It creates {@link ExcelCell} from the provided {@NodeView}
     * 
     * @param view
     * @param parent
     * @return
     */
    private List<ExcelCell> createCell(NodeView view, Node parentNode, Link parent) {
        return createCell(view, parentNode, parent, -1);
    }

    private List<ExcelCell> createCell(NodeView view, Node parentNode, Link parent, int duplicateColumnNumber) {
        return createCell(view, parentNode, parent, duplicateColumnNumber, false);
    }

    private List<ExcelCell> createCell(NodeView view, Node parentNode, Link parent, int duplicateColumnNumber,
            boolean shouldParentConsider) {

        if (view != null && view.getNode() != null) {

            if (isComplexType(view)) {
                List<ExcelCell> cells = new ArrayList<ExcelCell>();
                boolean isRequired = true;
                return createComplexCell(cells, view, parent, duplicateColumnNumber, 0, true, isRequired);
            } else {
                ExcelCell cell = createElementCell(view, parent, parentNode, -1, shouldParentConsider, false);
                if (cell != null) {
                    List<ExcelCell> cells = new ArrayList<ExcelCell>();
                    cells.add(cell);
                    return cells;
                }
            }
        }

        return null;
    }

    private List<ExcelCell> createComplexCell(List<ExcelCell> cells, NodeView view, Link superParent,
            int duplicateColumnNumber, int level) {
        return createComplexCell(cells, view, superParent, duplicateColumnNumber, level, false, false);
    }

    private boolean isComplexType(NodeView view) {
        return (view != null && StringUtils.isNotBlank(view.getComplexNodeTypeName())) ? true : false;
    }

    /**
     * It creates ExcelCell from the Complex-Type where nodes are definied into Hierarchical structure and need to
     * create list of Excelcell out of it.
     * 
     * @param cells
     * @param view
     * @param superParent
     * @param duplicateColumnNumber
     * @return
     */
    private List<ExcelCell> createComplexCell(List<ExcelCell> cells, NodeView view, Link superParent,
            int duplicateColumnNumber, int level, boolean shouldConsiderParent, boolean isParentRequired) {

        if (view != null & view.getNode() != null && view.getNode().getGroup() != null) {
            int child = findImmediatePrimitiveChild(view);
            Link parent = new Link(superParent, view.getNodeTagName());
            if (StringUtils.isNotBlank(view.getType())) {
                view.getNode().setDoucument(view.getType());
            } // as we had hack: sometime, documentation comes at View level and we don't have placeholder at view so,
              // setDocumentation at type

            if (child > 0) {

                if (level <= 2) { // TODO: CCM configurable

                    // If we don't have multi occurrence then we don't want to
                    if (duplicateColumnNumber < 1) {
                        for (NodeView childView : view.getNode().getGroup().getViews()) {
                            ExcelCell cell = createElementCell(childView, parent, view.getNode(), -1,
                                    shouldConsiderParent, isParentRequired);
                            cells.add(cell);
                        }
                    } else {
                        // If we don't have multi occurrence then we don't want to
                        for (int i = 1; i <= duplicateColumnNumber; i++) {
                            for (NodeView childView : view.getNode().getGroup().getViews()) {
                                ExcelCell dupCell = createElementCell(childView, parent, view.getNode(), i,
                                        shouldConsiderParent, isParentRequired);
                                cells.add(dupCell);
                            }
                        }
                    }

                } else {
                    // TODO: remove below
                    // log.error("We don't support hierarchical structure more than 2 level");
                }

            } else {
                // TODO: hierarchical
                for (NodeView childView : view.getNode().getGroup().getViews()) {
                    int number = -1;
                    boolean maxOccurs = "unbounded".equalsIgnoreCase(childView.getMaxOccurs())
                            || "-1".equalsIgnoreCase(childView.getMaxOccurs());
                    boolean isRequired = isRequired(childView);

                    if (maxOccurs) {
                        number = 2;
                    }
                    createComplexCell(cells, childView, parent, number, level + 1, true, isRequired);
                }

            }

            return cells;
        }

        return null;
    }

    public static boolean isRequired(NodeView view) {

        if (view != null) {
            return !"0".equalsIgnoreCase(view.getMinOccurs());
        }

        return false;
    }

    /**
     * It finds out immediate children of given Node which are type of primitive. If any Child is not Primitive Type ->
     * then it return -1
     * 
     * @param view
     * @return
     */
    private int findImmediatePrimitiveChild(NodeView view) {

        if (view != null & view.getNode() != null && view.getNode().getGroup() != null) {

            int primitiveChildren = 0;

            for (NodeView child : view.getNode().getGroup().getViews()) {
                if (child.getNode() != null && !isComplexType(child)) {
                    primitiveChildren++;
                } else {
                    return -1;
                }
            }

            return primitiveChildren;
        }

        return -1;
    }

    /**
     * It creates {@ExcelCell} from the given simple Node element
     * 
     * @param view
     * @param parent
     * @param duplicateColumnNumber
     * @return
     */
    private ExcelCell createElementCell(NodeView view, Link parent, Node parentNode, int duplicateColumnNumber,
            boolean shouldParentConsider, boolean isParentRequired) {

        ExcelCell cell = new ExcelCell();

        Link current = new Link(parent, view.getNodeTagName());

        String displayText = getDisplayText(view.getNodeTagName(), parent, current);
        String min = view.getMinOccurs();
        boolean isRequiredField = StringUtils.isNotBlank(min) && min.trim().equalsIgnoreCase("1");
        cell.setRequired(isRequiredField);
        cell.setXmlName(view.getNodeTagName());
        cell.setRestriction(view.getNode().getRestriction());
        cell.setxPath(current);
        cell.setMinOccurs(view.getMinOccurs());
        cell.setMaxOccurs(view.getMaxOccurs());
        if (duplicateColumnNumber > 0) {
            displayText += " (#" + duplicateColumnNumber + ")";
        }
        boolean isRequired = (shouldParentConsider ? isParentRequired : isRequiredField);
        if (isRequired
                && duplicateColumnNumber <= 1 /* Only first element must be required and later on are optional */) {
            displayText = displayText + " *";
        } else {
            displayText = displayText + " (Optional)";
        }
        cell.setDisplayText(displayText);
        String document = view.getNode().getDoucument();
        if (parentNode != null && StringUtils.isNotBlank(parentNode.getDoucument())
                && !StringUtils.isNotBlank(view.getNode().getDoucument())) {
            document = parentNode.getDoucument();
        }
        cell.setDocumentation(document);

        return cell;
    }

    /**
     * It prepares Display text which will be shown into Excel sheet
     * 
     * @param nodeTagName
     * @param parent
     * @param current
     * @return
     */
    private String getDisplayText(String nodeTagName, Link parent, Link current) {

        String tagName = toPrettyName(nodeTagName);
        String delimiter = "-";

        if (!current.getRootParent().equals(parent)) {
            Link child = current.getRootParent().getChild();
            tagName = "";
            for (; child != null; child = child.getChild()) {
                if (LinkType.SUBCATEGORY.equals(child.getType())) {
                    continue;
                } // Let's not append Root/Category/Sub-category into displayText
                tagName = StringUtils.isNotBlank(tagName) ? tagName + delimiter + toPrettyName(child.getMyName())
                        : toPrettyName(child.getMyName());
            }
        }
        if (tagName.contains(delimiter)) {
            String[] tokens = tagName.split(delimiter);
            if (tokens.length > 2) {
                int size = tokens.length - 1;
                tagName = tokens[size - 1] + delimiter + tokens[size];
            }
        }
        return getDisplayText(tagName);
    }

    /**
     * It applies extra treatment to tagName which is according to product-requirement
     * 
     * @param tagName
     * @return
     */
    public static String getDisplayText(String tagName) {

        if (StringUtils.isNotBlank(tagName)) {
            if (tagName.startsWith("Is ")) {
                tagName = tagName.replace("Is ", "") + "?";
            }
        }

        return tagName;
    }

    /**
     * It converts name into pretty name ie: hasWarranty -> Has Warranty
     * 
     * @param nodeTagName
     * @return
     */
    private String toPrettyName(String nodeTagName) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1 $2";

        nodeTagName = nodeTagName.replaceAll(regex, replacement);
        String upper = nodeTagName.toUpperCase();
        nodeTagName = upper.charAt(0) + nodeTagName.substring(1);
        return nodeTagName;
    }

}
