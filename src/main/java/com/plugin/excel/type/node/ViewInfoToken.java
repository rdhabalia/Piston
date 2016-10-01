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
 * It stores tokens which will be used by NodeView to store customize information such as: netflix 
 * 
 * @author rdhabal
 *
 */
public enum ViewInfoToken {

    REQUIRED_COUNTRY("REQUIRED_COUNTRY");

    private final String value;

    ViewInfoToken(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ViewInfoToken fromValue(String v) {
        for (ViewInfoToken c : ViewInfoToken.values()) {

            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
