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

/**
 * 
 */
package com.plugin.excel.auto.test;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.plugin.excel.util.ExcelFormatterApiImpl;

/**
 * 
 * @author rdhabalia
 *
 */
@ContextConfiguration(locations = { "/appContext.xml" })
public class ExcelGenerationManagerTest extends AbstractTestNGSpringContextTests {

    private static final String VERSION = "1.0";
    @Autowired
    private ExcelFormatterApiImpl excelFormatter;

    @Test(enabled = true)
    public void testExcelFileGeneration() throws IOException {
        String targetDirectory = "src/main/resources/excel";
        excelFormatter.generateProductExcel(VERSION, "Root", targetDirectory);
        System.out.println("Checkout generated Excel template at :" + targetDirectory);
    }

}
