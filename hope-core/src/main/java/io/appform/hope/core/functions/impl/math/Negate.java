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
