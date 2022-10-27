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

import com.fasterxml.jackson.core.JsonPointer;
import io.appform.hope.core.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JsonPointerValue extends FunctionEvaluatableValue {

    private final String pointer;
    private final JsonPointer jsonPointer;

    /**
     * @param pointer Json path value
     */
    public JsonPointerValue(String pointer) {
        this.pointer = pointer;
        this.jsonPointer = JsonPointer.compile(pointer);
    }

    /**
     * @param function A function that evaluates to a json path
     */
    public JsonPointerValue(FunctionValue function) {
        super(function);
        this.pointer = null;
        this.jsonPointer = null;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
