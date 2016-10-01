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

package com.plugin.excel.xsd.node.store.api;

import java.util.Map;
import java.util.Set;

import com.plugin.excel.type.node.Node;

/**
 * 
 * It manages XSD node creation and building the node tree.
 * 
 * @author rdhabal
 *
 */
public interface XsdNodeParserManager {

    /**
     * Get all simple types parsed by the given XsdNodeParser
     * 
     * @param xsdParser
     * @return
     */
    Map<String, Node> getSimpleNodes(String version);

    /**
     * Get all Complex types parsed by the given XsdNodeParser
     * 
     * @param xsdParser
     * @return
     */
    Map<String, Node> getComplexNodes(String version);

    /**
     * It builds Node-tree for a given Complex Node
     * 
     * @param complexNodeType
     * @return
     */
    Node buildComplexNode(String version, String complexNodeType);

    /**
     * It returns all supported versions
     * 
     * @return
     */
    Set<String> getSupportedVersions();

}
