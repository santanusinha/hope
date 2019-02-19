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
import io.appform.hope.core.utils.FunctionHelpers;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 * Returns quotient {@link NumericValue} for division of evaluated {@link NumericValue} numerator by evaluated {@link NumericValue} denominator.
 */
@FunctionImplementation("math.div")
public class Divide extends HopeFunction<NumericValue> {
    private final Value num;
    private final Value den;

    public Divide(Value num, Value den) {
        this.num = num;
        this.den = den;
    }

    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        double numValue = Converters.numericValue(evaluationContext, num, 0).doubleValue();
        double denValue = Converters.numericValue(evaluationContext, den, 0).doubleValue();
        FunctionHelpers.checkArgument(evaluationContext, denValue != 0, "Denominator is zero!!");
        return new NumericValue(numValue / denValue);
    }
}
