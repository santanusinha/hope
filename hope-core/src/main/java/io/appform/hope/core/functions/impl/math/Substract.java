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

package io.appform.hope.core.functions.impl.math;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 * Returns {@link NumericValue} result after subtracting evaluated {@link NumericValue} rhs from evaluated {@link NumericValue} from lhs.
 */
@FunctionImplementation("math.sub")
public class Substract extends HopeFunction<NumericValue> {
    private final Value lhs;
    private final Value rhs;

    public Substract(Value lhs, Value rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        double lhsValue = Converters.numericValue(evaluationContext, lhs, 0).doubleValue();
        double rhsValue = Converters.numericValue(evaluationContext, rhs, 0).doubleValue();
        return new NumericValue(lhsValue - rhsValue);
    }
}
