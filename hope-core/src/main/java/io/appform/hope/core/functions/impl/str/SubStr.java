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
@FunctionImplementation("str.substr")
public class SubStr extends HopeFunction {
    private final Value arg;
    private final Value start;
    private final Value end;

    public SubStr(Value arg, Value start, Value end) {
        this.arg = arg;
        this.start = start;
        this.end = end;
    }

    @Override
    public Value apply(Evaluator.EvaluationContext evaluationContext) {
        int startIndex = Converters.numericValue(evaluationContext, start, 0).intValue();
        int endIndex = Converters.numericValue(evaluationContext, end, 0).intValue();
        final String argValue = Converters.stringValue(evaluationContext, arg, "");
        return new StringValue(argValue.substring(startIndex, endIndex));
    }
}
