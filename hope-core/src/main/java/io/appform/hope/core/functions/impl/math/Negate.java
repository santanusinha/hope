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
 * Returns negative {@link NumericValue} of evaluated {@link NumericValue} parameter.
 */
@FunctionImplementation("math.negate")
public class Negate extends HopeFunction<NumericValue> {

    private final Value param;

    public Negate(Value param) {
        this.param = param;
    }

    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        double value = Converters.numericValue(evaluationContext, param, 0)
                .doubleValue();
        return new NumericValue(-1 * value);
    }
}
