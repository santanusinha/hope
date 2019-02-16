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
import java.util.HashSet;

/**
 * Checks if (evaluated) lhs value is not present in (evaluated) rhs array. Returns {@link BooleanValue}.
 */
@FunctionImplementation("arr.not_in")
public class NotIn extends HopeFunction<BooleanValue> {
    private final Value lhs;
    private final Value rhs;

    public NotIn(Value lhs, Value rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final Object lhsValue = Converters.objectValue(evaluationContext, lhs, null);
        return new BooleanValue(
                null == lhsValue
                        || !new HashSet<>(
                        Converters.flattenArray(evaluationContext, rhs, Collections.emptyList())).contains(lhsValue));
    }
}
