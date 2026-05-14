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

package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests ensuring correctness of scalar and array path evaluation across all arr.*
 * functions, bulk evaluation, and evaluateFirst.
 *
 * These tests lock in the use of JacksonJsonNodeJsonProvider (rather than JacksonJsonProvider):
 * switching providers causes ClassCastException in explodeArray because ctx.read() would return
 * ArrayList instead of JsonNode.
 */
class JsonPathAllocationStormTest {

    final ObjectMapper mapper = new ObjectMapper();
    final FunctionRegistry functionRegistry;
    final Evaluator evaluator;

    JsonPathAllocationStormTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
        this.evaluator = new Evaluator(new InjectValueErrorHandlingStrategy());
    }

    @ParameterizedTest
    @MethodSource("scalarRules")
    @SneakyThrows
    void testScalarPathReads(final String json, final String rule, final boolean expected) {
        val node = mapper.readTree(json);
        val operator = parse(rule);
        assertEquals(expected, evaluator.evaluate(operator, node),
                     "rule [" + rule + "] on [" + json + "]");
    }

    static Stream<Arguments> scalarRules() {
        return Stream.of(
                Arguments.of("{\"name\": \"hope\"}", "\"$.name\" == \"hope\"", true),
                Arguments.of("{\"name\": \"hope\"}", "\"$.name\" == \"other\"", false),
                Arguments.of("{\"count\": 42}", "\"$.count\" == 42", true),
                Arguments.of("{\"count\": 42}", "\"$.count\" > 40", true),
                Arguments.of("{\"count\": 42}", "\"$.count\" < 40", false),
                Arguments.of("{\"active\": true}", "\"$.active\" == true", true),
                Arguments.of("{\"active\": false}", "\"$.active\" == false", true),
                Arguments.of("{\"a\": {\"b\": \"deep\"}}", "\"$.a.b\" == \"deep\"", true),
                Arguments.of("{\"x\": 1}", "\"$.missing\" == \"value\"", false),
                Arguments.of("{\"field\": null}", "\"$.field\" == \"something\"", false)
        );
    }

    @ParameterizedTest
    @MethodSource("arrFunctionRules")
    @SneakyThrows
    void testArrFunctions(final String json, final String rule, final boolean expected) {
        val node = mapper.readTree(json);
        assertEquals(expected, evaluator.evaluate(parse(rule), node),
                     "rule [" + rule + "] on [" + json + "]");
    }

    static Stream<Arguments> arrFunctionRules() {
        return Stream.of(
                Arguments.of("{\"haystack\": [1, 2, 3, 4, 8, 16], \"needle\": 2}",
                             "arr.in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{\"haystack\": [1, 2, 3, 4, 8, 16], \"needle\": 99}",
                             "arr.in(\"$.needle\", \"$.haystack\") == true", false),
                Arguments.of("{\"haystack\": [\"alpha\", \"beta\", \"gamma\"], \"needle\": \"beta\"}",
                             "arr.in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{\"val\": [1, 2, 4, 8, 16]}",
                             "arr.contains_any(\"$.val\", [2, 3]) == true", true),
                Arguments.of("{\"val\": [1, 2, 4, 8, 16]}",
                             "arr.contains_any(\"$.val\", [9, 7]) == true", false),
                Arguments.of("{\"val\": [1, 2, 4, 8, 16]}",
                             "arr.contains_all(\"$.val\", [2, 4]) == true", true),
                Arguments.of("{\"val\": [1, 2, 4, 8, 16]}",
                             "arr.contains_all(\"$.val\", [2, 3]) == true", false),
                Arguments.of("{\"empty\": [], \"nonempty\": [1, 2, 3]}",
                             "arr.is_empty(\"$.empty\") == true", true),
                Arguments.of("{\"empty\": [], \"nonempty\": [1, 2, 3]}",
                             "arr.is_empty(\"$.nonempty\") == true", false),
                Arguments.of("{\"items\": [10, 20, 30, 40, 50]}",
                             "arr.len(\"$.items\") == 5", true),
                Arguments.of("{\"items\": []}",
                             "arr.len(\"$.items\") == 0", true),
                Arguments.of("{\"haystack\": [1, 2, 3], \"needle\": 99}",
                             "arr.not_in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{\"haystack\": [1, 2, 3], \"needle\": 2}",
                             "arr.not_in(\"$.needle\", \"$.haystack\") == true", false)
        );
    }

    @Test
    @SneakyThrows
    void testBulkEvaluateAllPaths() {
        val node = mapper.readTree(
                "{\"value\": 20, \"string\": \"Hello\", \"flag\": true, \"tags\": [\"a\", \"b\", \"c\"]}");

        val rules = java.util.List.of(
                parse("\"$.value\" == 20"),
                parse("\"$.string\" == \"Hello\""),
                parse("\"$.flag\" == true"),
                parse("arr.in(\"$.string\", [\"Hello\", \"World\"]) == true"),
                parse("arr.contains_any(\"$.tags\", [\"b\", \"z\"]) == true"),
                parse("arr.len(\"$.tags\") == 3"),
                parse("arr.not_in(\"$.value\", [99, 100]) == true")
        );

        val results = evaluator.evaluate(rules, node);

        assertEquals(7, results.size());
        assertTrue(results.get(0));
        assertTrue(results.get(1));
        assertTrue(results.get(2));
        assertTrue(results.get(3));
        assertTrue(results.get(4));
        assertTrue(results.get(5));
        assertTrue(results.get(6));
    }

    @Test
    @SneakyThrows
    void testEvaluateFirstWithMixedRules() {
        val node = mapper.readTree("{\"status\": \"ACTIVE\", \"tags\": [\"vip\", \"premium\"]}");
        val engine = HopeLangEngine.builder()
                .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
                .build();

        val rules = java.util.List.of(
                engine.parse("\"$.status\" == \"INACTIVE\""),
                engine.parse("arr.in(\"$.status\", [\"ACTIVE\", \"PENDING\"]) == true"),
                engine.parse("arr.contains_any(\"$.tags\", [\"vip\"]) == true")
        );

        val first = engine.evaluateFirst(rules, node);
        assertTrue(first.isPresent(), "Expected a match");
        assertEquals(1, first.getAsInt(), "Second rule (index 1) should match first");
    }

    @SneakyThrows
    private io.appform.hope.core.Evaluatable parse(String rule) {
        return new HopeParser(new StringReader(rule)).parse(functionRegistry);
    }
}
