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

import io.appform.hope.core.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents an object. Other concrete value types can be converted to this if required.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ObjectValue extends EvaluatableValue<Object> {

    /**
     * @param value Object value
     */
    public ObjectValue(Object value) {
        super(value);
    }

    /**
     * @param pathValue A json path that will get evaluated to an object
     */
    public ObjectValue(JsonPathValue pathValue) {
        super(pathValue);
    }

    /**
     * @param pointerValue json pointer value
     */
    public ObjectValue(JsonPointerValue pointerValue) {
        super(pointerValue);
    }

    /**
     * @param function A function that evaluates to an object
     */
    public ObjectValue(FunctionValue function) {
        super(function);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

}
