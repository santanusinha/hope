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

package io.appform.hope.core.functions.impl.arr;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

import java.util.Collections;
import java.util.List;

/**
 * Checks if array is empty
 */
@FunctionImplementation("arr.is_empty")
public class IsEmpty extends HopeFunction<BooleanValue> {

    private final Value arr;

    public IsEmpty(Value arr) {
        this.arr = arr;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final List<Value> values = Converters.explodeArray(evaluationContext, arr, Collections.emptyList());
        return new BooleanValue(null == values || values.isEmpty());
    }
}
