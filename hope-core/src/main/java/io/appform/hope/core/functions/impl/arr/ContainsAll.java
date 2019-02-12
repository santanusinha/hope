package io.appform.hope.core.functions.impl.arr;

import com.google.common.collect.Sets;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 */
@FunctionImplementation("arr.contains_all")
public class ContainsAll extends HopeFunction<BooleanValue> {
    private final Value lhs;
    private final Value rhs;

    public ContainsAll(Value lhs, Value rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final Set<Object> lhsValues = new TreeSet<>(
                Converters.flattenArray(evaluationContext, lhs, Collections.emptyList()));
        final Set<Object> rhsValues = new HashSet<>(
                Converters.flattenArray(evaluationContext, rhs, Collections.emptyList()));
        return new BooleanValue(
                lhsValues.size() >= rhsValues.size()
                        && Sets.intersection(lhsValues, rhsValues).size() == rhsValues.size());
    }
}
