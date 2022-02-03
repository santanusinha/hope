/*
 * Copyright 2019. Santanu Sinha
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and limitations
 * under the License.
 */

package io.appform.hope.core;

import io.appform.hope.core.combiners.AndCombiner;
import io.appform.hope.core.combiners.OrCombiner;
import io.appform.hope.core.operators.*;
import io.appform.hope.core.values.*;

import java.util.function.Supplier;

/**
 * Adapter to reduce redudndant code for visitor implementations
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
    public T visit(JsonPointerValue jsonPointerValue) {
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
