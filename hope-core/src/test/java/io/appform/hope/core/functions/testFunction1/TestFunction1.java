package io.appform.hope.core.functions.testFunction1;

import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

@FunctionImplementation("testFunction1")
public class TestFunction1 extends HopeFunction<BooleanValue> {
    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        return null;
    }
}
