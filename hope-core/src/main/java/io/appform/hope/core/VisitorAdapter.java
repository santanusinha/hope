package io.appform.hope.core;

import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.operators.*;
import io.appform.hope.core.values.*;

/**
 *
 */
public abstract class VisitorAdapter<T> implements Visitor<T> {

    private final T defaultValue;

    protected VisitorAdapter(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T visit(AndCombiner andCombiner) {
        return defaultValue;
    }

    @Override
    public T visit(OrCombiner orCombiner) {
        return defaultValue;
    }

    @Override
    public T visit(And and) {
        return defaultValue;
    }

    @Override
    public T visit(Equals equals) {
        return defaultValue;
    }

    @Override
    public T visit(Greater greater) {
        return defaultValue;
    }

    @Override
    public T visit(GreaterEquals greaterEquals) {
        return defaultValue;
    }

    @Override
    public T visit(Lesser lesser) {
        return defaultValue;
    }

    @Override
    public T visit(LesserEquals lesserEquals) {
        return defaultValue;
    }

    @Override
    public T visit(NotEquals notEquals) {
        return defaultValue;
    }

    @Override
    public T visit(Or or) {
        return defaultValue;
    }

    @Override
    public T visit(JsonPathValue jsonPathValue) {
        return defaultValue;
    }

    @Override
    public T visit(ObjectValue objectValue) {
        return defaultValue;
    }

    @Override
    public T visit(NumericValue numericValue) {
        return defaultValue;
    }

    @Override
    public T visit(StringValue stringValue) {
        return defaultValue;
    }

    @Override
    public T visit(BooleanValue booleanValue) {
        return defaultValue;
    }

    @Override
    public T visit(Not not) {
        return defaultValue;
    }
}
