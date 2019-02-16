/*
 * Copyright 2019. Santanu Sinha
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package io.appform.hope.core.exceptions.impl;

import io.appform.hope.core.exceptions.HopeException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * This exception is thrown in case there is an error in type validation in provided json.
 * This is thrown by {@link io.appform.hope.core.exceptions.errorstrategy.DefaultErrorHandlingStrategy#handleTypeMismatch(String, String, String, Object)}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HopeTypeMismatchError extends HopeException {
    private final String path;
    private final String expected;
    private final String actual;

    public HopeTypeMismatchError(String path, String expected, String actual) {
        super(String.format("Type mismatch at path %s. Expected: %s Actual: %s",
                            path, expected, actual));
        this.path = path;
        this.expected = expected;
        this.actual = actual;
    }
}
