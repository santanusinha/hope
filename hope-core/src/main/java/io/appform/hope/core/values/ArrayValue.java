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

package io.appform.hope.core.values;

import io.appform.hope.core.Value;
import io.appform.hope.core.Visitor;

import java.util.List;

/**
 * Represents an array os {@link Value}s.
 */
public class ArrayValue extends EvaluatableValue<List<Value>> {

    /**
     * @param value List of values
     */
    public ArrayValue(List<Value> value) {
        super(value);
    }

    /**
     * @param pathValue A json path that will get evaluated to an array
     */
    public ArrayValue(JsonPathValue pathValue) {
        super(pathValue);
    }

    /**
     * @param pointerValue A json pointer that will get evaluated to an array
     */
    public ArrayValue(JsonPointerValue pointerValue) {
        super(pointerValue);
    }

    /**
     * @param function A function that evaluates to an array
     */
    public ArrayValue(FunctionValue function) {
        super(function);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
