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

/**
 * A visitor to all leaf {@link TreeNode} types so that we don't have to subclass like apes.
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

    T visit(JsonPointerValue jsonPointerValue);

    T visit(ObjectValue objectValue);

    T visit(NumericValue numericValue);

    T visit(StringValue stringValue);

    T visit(BooleanValue booleanValue);

    T visit(Not not);

    T visit(FunctionValue functionValue);

    T visit(ArrayValue arrayValue);
}
