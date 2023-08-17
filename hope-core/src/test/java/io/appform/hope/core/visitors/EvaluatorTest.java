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

package io.appform.hope.core.visitors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.appform.hope.core.operators.Equals;
import io.appform.hope.core.values.ObjectValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class EvaluatorTest {

    final JsonNode node = NullNode.getInstance();

    @Test
    void basicTest() {
        final Evaluator evaluator = new Evaluator();
        assertTrue(
                evaluator.evaluate(
                        Equals.builder()
                                .lhs(new ObjectValue("a"))
                                .rhs(new ObjectValue("a"))
                                .build()
                        , node))
        ;
        assertFalse(
                evaluator.evaluate(
                        Equals.builder()
                                .lhs(new ObjectValue("a"))
                                .rhs(new ObjectValue("b"))
                                .build()
                        , node));
    }

}