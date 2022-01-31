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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * An abstraction of a value that can be evaluated from {@link JsonPathValue} or from {@link FunctionValue}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class EvaluatableValue<T> extends FunctionEvaluatableValue {
    private final T value;
    private final JsonPathValue pathValue;
    private final JsonPointerValue pointerValue;

    protected EvaluatableValue(T value) {
        super();
        this.value = value;
        this.pathValue = null;
        this.pointerValue = null;
    }

    protected EvaluatableValue(JsonPathValue pathValue) {
        super();
        this.value = null;
        this.pathValue = pathValue;
        this.pointerValue = null;
    }

    protected EvaluatableValue(JsonPointerValue pointerValue) {
        super();
        this.value = null;
        this.pathValue = null;
        this.pointerValue = pointerValue;
    }

    protected EvaluatableValue(FunctionValue function) {
        super(function);
        this.value = null;
        this.pathValue = null;
        this.pointerValue = null;
    }
}
