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

import com.google.common.collect.Sets;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks if (evaluated) lhs array contains any value from (evaluated) rhs array. Returns {@link BooleanValue}.
 */
@Slf4j
@FunctionImplementation("arr.contains_any")
public class ContainsAny extends HopeFunction<BooleanValue> {
    private final Value lhs;
    private final Value rhs;

    public ContainsAny(Value lhs, Value rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        Set<Object> lhsValues = new TreeSet<>(Converters.flattenArray(evaluationContext, lhs, Collections.emptyList()));
        Set<Object> rhsValues = new HashSet<>(Converters.flattenArray(evaluationContext, rhs, Collections.emptyList()));
        log.debug("LHS: {}", lhsValues);
        log.debug("RHS: {}", rhsValues);
        return new BooleanValue(!Sets.intersection(lhsValues, rhsValues).isEmpty());
    }
}
