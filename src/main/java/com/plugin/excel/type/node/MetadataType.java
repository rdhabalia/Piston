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
 * @author rdhabal
 *
 */
public enum MetadataType {

    BASE_ATTRIBUTE("USER_ATTRIBUTE"), MEDIA_1("MEDIA_1"), MEDIA_2("MEDIA_2"), MEDIA_3(
            "MEDIA_3"), OTHER("OTHER");

    private final String value;

    MetadataType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MetadataType fromValue(String v) {
        for (MetadataType c : MetadataType.values()) {

            if (c.value.equals(v)) {
                return c;
            }
        }
        return null;
    }

}
