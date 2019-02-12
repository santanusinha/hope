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
 *
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
