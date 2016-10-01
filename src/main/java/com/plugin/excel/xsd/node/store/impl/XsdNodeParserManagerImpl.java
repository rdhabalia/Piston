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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.plugin.excel.type.node.Node;
import com.plugin.excel.xsd.node.store.api.XsdNodeParserManager;

/**
 * 
 * @author rdhabal
 *
 */
public class XsdNodeParserManagerImpl implements XsdNodeParserManager {

    private Map<String, XsdNodeParser> xsdParsers = new ConcurrentHashMap<String, XsdNodeParser>();

    public XsdNodeParserManagerImpl(String directoryLocation, boolean readFromResource, String ignoreDirectories) {

        try {
            File parentDirectory = null;
            if (readFromResource) {

                /**
                 * XSOMParser Parser parses relatives XSD only if it is provided in form of {@link File} and InputStream
                 * throws NPE due to relative import XSDs.
                 * 
                 * We can't get File object pointer for any File in Jar. but we can traverse Jar file by file. So, we
                 * will traverse jar, read as input stream, create a temp directory and copies all the file there and
                 * get the File Object pointer.
                 */
                parentDirectory = File.createTempFile(directoryLocation, "temp");
                parentDirectory.delete();
                parentDirectory.deleteOnExit();
                List<String> files = getXsdPaths(directoryLocation);
                parentDirectory = FileHelper.copyFolder(this.getClass(), files, parentDirectory, directoryLocation);

            } else {
                parentDirectory = new File(directoryLocation);
            }

            initializeParsers(parentDirectory, ignoreDirectories);
        } catch (Exception e) {
            // TODO: log
            String msg = "Couldn't parse XSDs from the location: " + directoryLocation + ", " + e.getMessage();
            throw new IllegalArgumentException(msg, e);
        }
    }

    private void initializeParsers(File dir, String ignoreDirectories) {

        if (dir != null && dir.isDirectory()) {
            List<File> dirs = FileHelper.findImmediateChildDirectory(dir);
            if (dirs != null && !dirs.isEmpty()) {
                for (File file : dirs) {
                    String version = deriveVersion(file.getName());
                    XsdNodeParser parser = new XsdNodeParser(file, ignoreDirectories);
                    xsdParsers.put(version, parser);
                }
            }
        } else {
            // TODO: log and throw exception
        }

    }

    private String deriveVersion(String dir) {

        if (StringUtils.isNotBlank(dir)) {
            return dir.replace("_", ".").replace("V", "").replace("v", "");
        }

        return null;
    }

    public Map<String, Node> getSimpleNodes(String version) {
        return xsdParsers.get(version) != null ? xsdParsers.get(version).getSimpleNodes() : null;
    }

    public Map<String, Node> getComplexNodes(String version) {
        return xsdParsers.get(version) != null ? xsdParsers.get(version).getComplexNodes() : null;
    }

    public Node buildComplexNode(String version, String complexNodeType) {
        return xsdParsers.get(version) != null ? xsdParsers.get(version).buildComplexNode(complexNodeType) : null;
    }

    public Set<String> getSupportedVersions() {
        return xsdParsers != null ? xsdParsers.keySet() : null;
    }

    private List<String> getXsdPaths(String directoryLocation) throws IOException {

        List<String> files = new ArrayList<String>();

        Enumeration<URL> e = this.getClass().getClassLoader().getResources(directoryLocation);
        while (e.hasMoreElements()) {
            java.net.URL url = e.nextElement();
            java.net.JarURLConnection urlcon = (java.net.JarURLConnection) (url.openConnection());
            java.util.jar.JarFile jar = urlcon.getJarFile();
            {
                java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    String entry = entries.nextElement().getName();
                    if (entry.startsWith(directoryLocation)) {
                        files.add(entry);
                    }
                }
            }
        }

        return files;
    }

}
