package io.appform.hope.core.functions.impl.str;

import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.StringValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
 */
@FunctionImplementation("str.upper")
public class UpperCase extends HopeFunction {
    private final Value arg;

    public UpperCase(Value arg) {
        this.arg = arg;
    }

    @Override
    public Value apply(Evaluator.EvaluationContext evaluationContext) {
        return new StringValue(Converters.stringValue(evaluationContext, arg, "").toUpperCase());
    }
}
