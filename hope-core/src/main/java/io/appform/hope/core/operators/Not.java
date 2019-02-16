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

import io.appform.hope.core.UnaryOperator;
import io.appform.hope.core.Visitor;
import io.appform.hope.core.values.BooleanValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Unary operator to invert a {@link BooleanValue}
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Not extends UnaryOperator<BooleanValue> {

    @Builder
    public Not(BooleanValue operand) {
        super(operand);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }
}
