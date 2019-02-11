package io.appform.hope.core;

import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.operators.*;
import io.appform.hope.core.values.*;

/**
 *
 */
public interface Visitor<T> {
    T visit(AndCombiner andCombiner);

    T visit(OrCombiner orCombiner);

    T visit(And and);

    T visit(Equals equals);

    T visit(Greater greater);

    T visit(GreaterEquals greaterEquals);

    T visit(Lesser lesser);

    T visit(LesserEquals lesserEquals);

    T visit(NotEquals notEquals);

    T visit(Or or);

    T visit(JsonPathValue jsonPathValue);

    T visit(ObjectValue objectValue);

    T visit(NumericValue numericValue);

    T visit(StringValue stringValue);

    T visit(BooleanValue booleanValue);

    T visit(Not not);
}
