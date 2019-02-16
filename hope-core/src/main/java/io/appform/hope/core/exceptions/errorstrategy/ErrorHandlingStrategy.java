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

package io.appform.hope.core.exceptions.errorstrategy;

/**
 * This class is called in error situations. This lets' users configure the behaviour in scenarios where these
 * scenarios appear.
 */
public interface ErrorHandlingStrategy {

    /**
     * This handler is invoked when a value cannot be resolved from a path.
     * This happens when value is not present or null.
     * @param path Path for which value was not found
     * @param defaultValue Default value to be returned if present
     * @param <T> Type of default Value
     * @return defaultValue or exception depending on implementation
     */
    <T> T handleMissingValue(String path, T defaultValue);

    /**
     * This handler is invoked when there is type mismatch between value at path and that present in json.
     * @param path Path for which value mismatch was detected
     * @param expected Expected type
     * @param actual Type found in provided json
     * @param defaultValue Default value to be returned in case of mismatch
     * @param <T> Type of defaultValue
     * @return defaultValue or throws exception depending on implementation
     */
    <T> T handleTypeMismatch(String path, String expected, String actual, T defaultValue);

    /**
     * This handler is called in case there is some bug in the evaluation.
     * This should never be raised faced in production. In case this happens it is a bug that should be reported.
     * @param message Custom message provided at assertion site
     * @param defaultValue Default value to be returned if this state is faced
     * @param <T> Type of defaultValue
     * @return defaultValue or throws exception depending on implementation
     */
    <T> T handleIllegalEval(String message, T defaultValue);

}
