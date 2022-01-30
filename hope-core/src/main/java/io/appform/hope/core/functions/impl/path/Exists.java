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

package io.appform.hope.core.functions.impl.path;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 * Returns true {@link BooleanValue} if a json node/value exists at provided path.
 */
@FunctionImplementation("path.exists")
public class Exists extends HopeFunction<BooleanValue> {

    private final Value path;

    public Exists(Value path) {
        this.path = path;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final String pathValue = Converters.jsonPathValue(evaluationContext, path, "");
        if(Strings.isNullOrEmpty(pathValue)) {
            return new BooleanValue(false);
        }
        final JsonNode node = evaluationContext.getJsonContext()
                .at(Converters.toJsonPointer(pathValue));
        if(null == node || node.isNull()) {
            return new BooleanValue(false);
        }
        return new BooleanValue(true);
    }
}
