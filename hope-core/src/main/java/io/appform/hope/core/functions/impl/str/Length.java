package io.appform.hope.core.functions.impl.str;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
 */
@FunctionImplementation("str.len")
public class Length extends HopeFunction {
    private final Value arg;

    public Length(Value arg) {
        this.arg = arg;
    }

    @Override
    public Value apply(Evaluator.EvaluationContext evaluationContext) {
        return new NumericValue((double)Converters.stringValue(evaluationContext, arg, "").length());
    }
}
