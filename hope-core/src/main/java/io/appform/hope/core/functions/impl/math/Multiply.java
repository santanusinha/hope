package io.appform.hope.core.functions.impl.math;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

import java.util.Arrays;

/**
 *
 */
@FunctionImplementation("math.prod")
public class Multiply extends HopeFunction<NumericValue> {

    private final Value[] values;

    public Multiply(Value ... values) {
        this.values = values;
    }

    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {

        return new NumericValue(Arrays.stream(values)
                .mapToDouble(value -> Converters.numericValue(evaluationContext, value, 0)
                        .doubleValue())
                .reduce(1, (res, x) -> res * x));
    }
}
