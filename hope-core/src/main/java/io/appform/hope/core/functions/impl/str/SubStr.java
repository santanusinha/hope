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

package io.appform.hope.core.functions.impl.str;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.StringValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 * Returns substring {@link StringValue} of provided parameter {@link StringValue}
 * from {@link io.appform.hope.core.values.NumericValue} start
 * to {@link io.appform.hope.core.values.NumericValue} end (exclusive).
 */
@FunctionImplementation("str.substr")
public class SubStr extends HopeFunction {
    private final Value arg;
    private final Value start;
    private final Value end;

    public SubStr(Value arg, Value start, Value end) {
        this.arg = arg;
        this.start = start;
        this.end = end;
    }

    @Override
    public Value apply(Evaluator.EvaluationContext evaluationContext) {
        int startIndex = Converters.numericValue(evaluationContext, start, 0).intValue();
        int endIndex = Converters.numericValue(evaluationContext, end, 0).intValue();
        final String argValue = Converters.stringValue(evaluationContext, arg, "");
        return new StringValue(argValue.substring(startIndex, endIndex));
    }
}
