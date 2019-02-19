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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 * Matches a string against a regex
 */
@FunctionImplementation("str.match")
public class Match extends HopeFunction<BooleanValue> {
    private final Value regex;
    private final Value str;

    public Match(Value regex, Value str) {
        this.regex = regex;
        this.str = str;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final String regexValue = Converters.stringValue(evaluationContext, regex, "");
        final String target = Converters.stringValue(evaluationContext, str, "");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(regexValue));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(target));
        return new BooleanValue(target.matches(regexValue));
    }
}
