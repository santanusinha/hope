package io.appform.hope.core.functions.impl.math;

import com.google.common.base.Preconditions;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
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
        Preconditions.checkArgument(denValue != 0, "Denominator is zero!!");
        return new NumericValue(numValue / denValue);
    }
}
