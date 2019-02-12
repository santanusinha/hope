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
 *
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
        log.info("LHS: {}", lhsValues);
        log.info("RHS: {}", rhsValues);
        return new BooleanValue(!Sets.intersection(lhsValues, rhsValues).isEmpty());
    }
}
