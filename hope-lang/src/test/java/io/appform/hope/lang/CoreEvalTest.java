/*
 * Copyright 2022. Santanu Sinha
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

package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests core evaluation functionality
 */
class CoreEvalTest {
    final ObjectMapper mapper = new ObjectMapper();

    final JsonNode node = NullNode.getInstance();
    final FunctionRegistry functionRegistry;

    CoreEvalTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "23 == 23",
                    "23 != 29",
                    "true & true",
                    "true | true",
                    "true | false",
                    "false | true",
                    "3 > 2",
                    "3 >= 2",
                    "2 < 3",
                    "2 <= 3",
                    "3 >= 2 && 2 < 5",
                    "3 <= 2 || 2 < 5",
                    "3 > 2 || 2 > 5",
                    "(3 == 2) || (2 < 5)",
                    "((3 >= 2) && (2 < 5)) || 2 == 2 ",
                    "(((3 >= 2) || (2 == 5)) && ((2 == 2) ||  (2 == 1)))",
                    "'abc' == 'abc'",
                    "\"abc\" == \"abc\"",
                    "'abc' == \"abc\"",
                    "\"abc\" == 'abc'",
                    "\"a b c\" == 'a b c'"
            }
    )
    void testBasicSuccess(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                    "23 == 29",
                    "23 != 23",
                    "true & false",
                    "false | false",
                    "2 > 3",
                    "2 >= 3",
                    "3 < 2",
                    "3 <= 2",
                    "(3 <= 2)",
                    "(3 == 2) && (2 < 5)",
                    "(((3 >= 2) && (2 == 5)) && 2 == 2)",
                    "(((3 <= 2) || (2 == 5)) && ((2 == 2) ||  (2 == 1)))"
            }
    )
    void testBasicFail(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @ParameterizedTest
    @MethodSource("rules")
    @SneakyThrows
    void testRules(final String json, final String rule, boolean expectation) {
        val node = mapper.readTree(json);
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);

        assertEquals(expectation, new Evaluator().evaluate(operator, node));
    }

    private static Stream<Arguments> rules() {
        return Stream.of(
                Arguments.of("{ \"first name\" : \"santanu\" }", "\"/first name\" == \"santanu\"", true),
                Arguments.of("{ \"first name\" : \"santanu\" }", "'/first name' == \"santanu\"", true),
                Arguments.of("{ \"name\" : \"santanu\" }", "\"$.name\" == \"santanu\"", true),
                Arguments.of("{ \"name\" : \"santanu\" }", "'$.name' == 'santanu'", true),
                Arguments.of("{ \"name\" : \"santanu\" }", "\"/name\" == \"santanu\"", true),
                Arguments.of("{ \"name\" : \"santanu\" }", "'/name' == 'santanu'", true),
                Arguments.of("{ \"count\" : 93 }", "\"$.count\" == 93", true),
                Arguments.of("{ \"count\" : 93 }", "\"/count\" == 93", true),
                Arguments.of("{ \"count\" : 93 }", "'/count' == 93", true),
                Arguments.of("{ \"count\" : 93 }", "94 > \"$.count\"", true),
                Arguments.of("{ \"count\" : 93 }", "94 > '$.count'", true),
                Arguments.of("{ \"count\" : 93 }", "94 > \"/count\"", true),
                Arguments.of("{ \"count\" : 93 }", "94 > '/count'", true),
                Arguments.of("{ \"count\" : 97 }", "\"$.count\" > 94", true),
                Arguments.of("{ \"count\" : 97 }", "'$.count' > 94", true),
                Arguments.of("{ \"count\" : 97 }", "\"/count\" > 94", true),
                Arguments.of("{ \"count\" : 97 }", "'/count' > 94", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "\"$.lhs\" > \"$.rhs\"", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'$.lhs' > '$.rhs'", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "\"/lhs\" > \"/rhs\"", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'/lhs' > '/rhs'", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'/lhs' > '$.rhs'", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'/lhs' > \"$.rhs\"", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "\"$.lhs\" > \"/rhs\"", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'/lhs' > '/rhs'", true),
                Arguments.of("{ \"lhs\" : 97, \"rhs\" : 94 }", "'/lhs' > '/rhs'", true),
                Arguments.of("{ \"count\" : 93 }", "92 < \"$.count\"", true),
                Arguments.of("{ \"count\" : 93 }", "92 < '$.count'", true),
                Arguments.of("{ \"count\" : 91 }", "\"$.count\" < 94", true),
                Arguments.of("{ \"count\" : 91 }", "'$.count' < 94", true),
                Arguments.of("{ \"count\" : 91 }", "\"/count\" < 94", true),
                Arguments.of("{ \"count\" : 91 }", "'/count' < 94", true),
                Arguments.of("{ \"count\" : 91 }", "'/count' < 94", true),
                Arguments.of("{ \"lhs\" : 94, \"rhs\" : 97 }", "\"$.lhs\" < \"$.rhs\"", true),
                Arguments.of("{ \"lhs\" : 94, \"rhs\" : 97 }", "'$.lhs' < '$.rhs'", true),
                Arguments.of("{ \"lhs\" : 94, \"rhs\" : 97 }", "\"$.lhs\" < \"/rhs\"", true),
                Arguments.of("{ \"lhs\" : 94, \"rhs\" : 97 }", "'/lhs' < '/rhs'", true),
                Arguments.of("{ \"lhs\" : 94, \"rhs\" : 97 }", "\"$.lhs\" < '/rhs'", true),
                Arguments.of("{ \"boolValue\" : false }", "^false", true),
                Arguments.of("{ \"boolValue\" : false }", "^\"$.boolValue\"", true),
                Arguments.of("{ \"boolValue\" : false }", "^'$.boolValue'", true),
                Arguments.of("{ \"boolValue\" : false }", "^\"/boolValue\"", true),
                Arguments.of("{ \"boolValue\" : false }", "^'/boolValue'", true),
                Arguments.of("{ \"boolValue\" : false }", "(^\"/boolValue\")", true),
                Arguments.of("{ \"boolValue\" : false }", "(^'$.boolValue')", true),
                Arguments.of("{ \"boolValue\" : false, \"num\" : 43 }", "(^\"$.boolValue\" && \"$.num\" > 40)", true),
                Arguments.of("{ \"boolValue\" : false, \"num\" : 43 }", "(^'$.boolValue' && '$.num' > 40)", true),
                Arguments.of("{ \"boolValue\" : false, \"num\" : 43 }", "(^\"/boolValue\" && \"/num\" > 40)", true),
                Arguments.of("{ \"boolValue\" : false, \"num\" : 43 }", "(^'/boolValue' && '/num' > 40)", true),
                Arguments.of("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}", "(^\"$.boolValue\" && (\"$.num1\" > 45 || \"$.num2\" < 50))", true),
                Arguments.of("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}", "(^'$.boolValue' && ('$.num1' > 45 || '$.num2' < 50))", true),
                Arguments.of("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}", "(^\"/boolValue\" && (\"/num1\" > 45 || \"/num2\" < 50))", true),
                Arguments.of("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}", "(^'/boolValue' && ('/num1' > 45 || '/num2' < 50))", true)
                );
    }
}
