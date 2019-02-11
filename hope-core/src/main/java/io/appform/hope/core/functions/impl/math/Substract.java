package io.appform.hope.core.functions.impl.math;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
 */
@FunctionImplementation("math.minus")
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
