package io.appform.hope.core.functions;

import com.google.common.reflect.TypeToken;
import io.appform.hope.core.Value;
import io.appform.hope.core.visitors.Evaluator;
import lombok.Getter;

/**
 *
 */
public abstract class HopeFunction<T extends Value> {

    @Getter
    private TypeToken<T> returnType = new TypeToken<T>(getClass()) { };

    public abstract T apply(Evaluator.EvaluationContext evaluationContext);
}
