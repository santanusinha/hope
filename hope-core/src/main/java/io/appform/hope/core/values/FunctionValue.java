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
import io.appform.hope.core.functions.FunctionRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * An abstraction for a {@link io.appform.hope.core.functions.HopeFunction} call.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class FunctionValue extends Value {
    private final String name;
    private final List<Value> parameters;
    private final FunctionRegistry.ConstructorMeta selectedConstructor;

    /**
     * @param name                Name of the function as provided to {@link io.appform.hope.core.functions.FunctionImplementation}
     * @param parameters          Parameters to be passed to the function
     * @param selectedConstructor Selected overload of the function from {@link FunctionRegistry}
     */
    public FunctionValue(String name, List<Value> parameters, FunctionRegistry.ConstructorMeta selectedConstructor) {
        this.name = name;
        this.parameters = parameters;
        this.selectedConstructor = selectedConstructor;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
