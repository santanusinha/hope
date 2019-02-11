package io.appform.hope.core.functions.impl.sys;

import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
 */
@FunctionImplementation("sys.epoch")
public class Epoch extends HopeFunction<NumericValue> {
    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        return new NumericValue(System.currentTimeMillis());
    }
}
