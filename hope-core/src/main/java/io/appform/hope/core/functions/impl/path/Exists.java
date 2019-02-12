package io.appform.hope.core.functions.impl.path;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Strings;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.visitors.Evaluator;

/**
 *
 */
@FunctionImplementation("path.exists")
public class Exists extends HopeFunction<BooleanValue> {

    private final Value path;

    public Exists(Value path) {
        this.path = path;
    }

    @Override
    public BooleanValue apply(Evaluator.EvaluationContext evaluationContext) {
        final String pathValue = Converters.jsonPathValue(evaluationContext, path, "");
        if(Strings.isNullOrEmpty(pathValue)) {
            return new BooleanValue(false);
        }
        final JsonNode node = evaluationContext.getJsonContext()
                .read(pathValue);
        if(node.isNull()) {
            return new BooleanValue(false);
        }
        return new BooleanValue(true);
    }
}
