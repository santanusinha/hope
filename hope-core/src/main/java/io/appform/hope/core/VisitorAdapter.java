package io.appform.hope.core;

import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.operators.*;
import io.appform.hope.core.values.*;

import java.util.function.Supplier;

/**
 *
 */
public abstract class VisitorAdapter<T> implements Visitor<T> {

    private final Supplier<T> defaultValueGenerator;

    protected VisitorAdapter(Supplier<T> defaultValueGenerator) {
        this.defaultValueGenerator = defaultValueGenerator;
    }

    @Override
    public T visit(AndCombiner andCombiner) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(OrCombiner orCombiner) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(And and) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(Equals equals) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(Greater greater) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(GreaterEquals greaterEquals) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(Lesser lesser) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(LesserEquals lesserEquals) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(NotEquals notEquals) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(Or or) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(JsonPathValue jsonPathValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(ObjectValue objectValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(NumericValue numericValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(StringValue stringValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(BooleanValue booleanValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(Not not) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(FunctionValue functionValue) {
        return defaultValueGenerator.get();
    }

    @Override
    public T visit(ArrayValue arrayValue) {
        return defaultValueGenerator.get();
    }
}
