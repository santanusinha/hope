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

package io.appform.hope.core.functions.impl.date;

import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

import java.time.LocalDateTime;

/**
 * Returns the current hour of the day as a NumericValue.
 */
@FunctionImplementation("date.hour_of_day")
public class HourOfDay extends HopeFunction<NumericValue> {
    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        return new NumericValue(LocalDateTime.now().getHour());
    }
}
