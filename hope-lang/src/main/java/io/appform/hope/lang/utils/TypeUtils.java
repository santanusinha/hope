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

package io.appform.hope.lang.utils;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionRegistry;

import java.util.List;

/**
 * A bunch of utils used by parser
 */
public class TypeUtils {
    private TypeUtils() {}

    public static FunctionRegistry.FunctionMeta function(FunctionRegistry functionRegistry, String name, List<Value> values) {
        final FunctionRegistry.FunctionMeta functionMeta = functionRegistry.find(name)
                .orElse(null);
        if (null == functionMeta) {
            throw new IllegalArgumentException("Unknown function '" + name + "'");
        }
        if(functionMeta.isArrayValue()) {
            return functionMeta;
        }
        final List<Class<?>> paramTypes = functionMeta.getParamTypes();
        if(!paramTypes.isEmpty()) {
            if (values.size() != paramTypes.size()) {
                throw new IllegalArgumentException(
                        String.format("Function '%s' expects '%d' arguments but '%d' provided",
                                      name, paramTypes.size(), values.size()));
            }
/*
            final List<Value> unmatched = IntStream.range(0, values.size())
                    .filter(i -> values.get(i).getClass().isAssignableFrom(paramTypes.get(i)))
                    .mapToObj(values::get)
                    .collect(Collectors.toList());
            Preconditions.checkArgument(
                    unmatched.isEmpty(),
                    String.format("Type mismatch for function params passed to %s. Required: %s, Provided: %s",
                                  name,
                                  paramTypes.stream()
                                          .map(Class::getSimpleName)
                                          .collect(Collectors.toList()),
                                  values.stream()
                                          .map(Object::getClass)
                                          .map(Class::getSimpleName)
                                          .collect(Collectors.toList())));
*/
        }
        return functionMeta;
    }
}

