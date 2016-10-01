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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * It helps to deal with File read/write opearions
 * 
 * @author rdhabal
 *
 */
public class FileHelper {

    public static List<String> getAllXsds(String directoryLocation) {

        if (StringUtils.isNotBlank(directoryLocation)) {
            List<File> files = new ArrayList<File>();
            findFiles(directoryLocation, files);
            if (!files.isEmpty()) {
                List<String> fileStrs = new ArrayList<String>();
                for (File file : files) {
                    fileStrs.add(file.getPath());
                }
                return fileStrs;
            }
        }

        return null;
    }

    public static void findFiles(String directoryName, List<File> files) {
        File directory = new File(directoryName);
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                findFiles(file.getAbsolutePath(), files);
            }
        }
    }

    public static List<File> findImmediateChildDirectory(File directory) {

        List<File> files = new ArrayList<File>();
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                files.add(file);
            }
        }
        return files;
    }

    public static void findFiles(File directory, List<File> files) {
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                findFiles(file.getAbsolutePath(), files);
            }
        }
    }

    public static void copyFolder(File src, File dest) throws IOException {

        if (src.isDirectory()) {

            // if directory not exists, create it
            if (!dest.exists()) {
                dest.mkdir();
                // log.info("Directory copied from " + src + " to " + dest);
            }

            // list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                // construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // recursive copy
                copyFolder(srcFile, destFile);
            }

        } else {
            // if file, then copy it
            // Use bytes stream to support all file types
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            // copy the file content in bytes
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            // log.info("File copied from " + src + " to " + dest);
        }
    }

    public static File getFilePointer(InputStream source, String destName) {

        File destination;
        try {
            destination = File.createTempFile(destName, "temp");
            if (!(destination.delete())) {
                throw new IOException("Could not delete temp file: " + destination.getAbsolutePath());
            }
            if (!(destination.mkdir())) {
                throw new IOException("Could not create temp directory: " + destination.getAbsolutePath());
            }
            FileUtils.copyInputStreamToFile(source, destination);
            destination.deleteOnExit();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static File copyFolder(Class clazz, List<String> files, File destination, String rootDirName) {

        File rootDir = null;

        if (clazz != null && files != null && !files.isEmpty()) {

            for (String file : files) {
                InputStream stream = clazz.getClassLoader().getResourceAsStream(file);
                try {
                    File temp = new File(destination.getAbsoluteFile() + "/" + file);
                    if (!file.contains(".")) {
                        temp.delete();
                        temp.mkdir();
                    } else {
                        FileUtils.copyInputStreamToFile(stream, temp);
                    }

                    if (file.replaceAll("/", "").equalsIgnoreCase(rootDirName)) {
                        rootDir = temp;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return rootDir;
    }

}
