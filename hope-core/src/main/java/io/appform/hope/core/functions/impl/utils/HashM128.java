/*
 * Copyright 2020. Santanu Sinha
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

package io.appform.hope.core.functions.impl.utils;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.appform.hope.core.Value;
import io.appform.hope.core.functions.FunctionImplementation;
import io.appform.hope.core.functions.HopeFunction;
import io.appform.hope.core.utils.Converters;
import io.appform.hope.core.utils.RawTypeHandler;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.values.StringValue;
import io.appform.hope.core.visitors.Evaluator;
import lombok.val;

import java.nio.charset.Charset;

/**
 * Returns {@link NumericValue} Murmur3 128 hash of provided evaluated {@link StringValue} parameter.
 */
@FunctionImplementation("utils.hash_m128")
@SuppressWarnings("UnstableApiUsage")
public class HashM128 extends HopeFunction<NumericValue> {
    private final Value arg;

    public HashM128(Value arg) {
        this.arg = arg;
    }

    @Override
    public NumericValue apply(Evaluator.EvaluationContext evaluationContext) {
        val hasher = Hashing.murmur3_128().newHasher();
        Converters.handleValue(evaluationContext, arg, "", new RawTypeHandler<Hasher>() {
            @Override
            public Hasher handleString(String s) {
                return hasher.putString(s, Charset.defaultCharset());
            }

            @Override
            public Hasher handleBoolean(boolean b) {
                return hasher.putBoolean(b);
            }

            @Override
            public Hasher handleNumber(Number n) {
                return hasher.putDouble(n.doubleValue());
            }

            @Override
            public Hasher handleObject(Object o) {
                return hasher.putString(arg.toString(), Charset.defaultCharset());
            }
        });
        return new NumericValue(hasher.hash().asLong());
    }
}
