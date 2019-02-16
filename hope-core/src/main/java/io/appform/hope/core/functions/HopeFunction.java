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

package io.appform.hope.core.functions;

import com.google.common.reflect.TypeToken;
import io.appform.hope.core.Value;
import io.appform.hope.core.visitors.Evaluator;
import lombok.Getter;

/**
 * An abstraction for a function that takes a variable number (zero or more) of parameters that evaluate to {@link Value}
 * and returns a result {@link Value}.
 */
public abstract class HopeFunction<T extends Value> {

    @Getter
    private TypeToken<T> returnType = new TypeToken<T>(getClass()) { };

    public abstract T apply(Evaluator.EvaluationContext evaluationContext);
}
