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

package io.appform.hope.core.operators;

import io.appform.hope.core.BinaryOperator;
import io.appform.hope.core.Visitor;
import io.appform.hope.core.values.NumericValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Checks if lhs {@link NumericValue} is lesser than or equal to rhs {@link NumericValue}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LesserEquals extends BinaryOperator<NumericValue> {

    @Builder
    public LesserEquals(NumericValue lhs, NumericValue rhs) {
        super(lhs, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }


}
