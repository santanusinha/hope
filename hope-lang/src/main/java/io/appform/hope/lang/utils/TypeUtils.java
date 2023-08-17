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

import com.google.common.base.Preconditions;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.values.FunctionValue;

import java.util.List;

/**
 * A bunch of utils used by parser
 */
public class TypeUtils {
    private TypeUtils() {}

    public static FunctionValue function(FunctionRegistry functionRegistry, String name, List<Value> values) {
        final FunctionRegistry.FunctionMeta functionMeta = functionRegistry.find(name)
                .orElse(null);
        if (null == functionMeta) {
            throw new IllegalArgumentException("Unknown function '" + name + "'");
        }
        final List<FunctionRegistry.ConstructorMeta> constructors = functionMeta.getConstructors();
        if(constructors.stream().anyMatch(FunctionRegistry.ConstructorMeta::isHasVariableArgs)) {
            return new FunctionValue(name, values, constructors.get(0));
        }
        final int numProvidedParams = values.size();
        final List<FunctionRegistry.ConstructorMeta> matchingConstructors = constructors.stream()
                .filter(constructorMeta -> constructorMeta.getParamTypes()
                        .size() == numProvidedParams)
                .toList();
        Preconditions.checkArgument(!matchingConstructors.isEmpty(),
                                    String.format("No matching function named %s that accepts %d params.",
                                                  name, numProvidedParams));
        Preconditions.checkArgument(matchingConstructors.size() == 1,
                                    "Function " + name + " seems to have more than one matching overload." +
                                            " Cannot resolve.");

        return new FunctionValue(name, values, matchingConstructors.get(0));
    }
}

