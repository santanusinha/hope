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
 * Regression tests for the JacksonJsonNodeJsonProvider allocation storm issue.
 *
 * JacksonJsonNodeJsonProvider calls objectMapper.valueToTree() for every intermediate
 * path element during JsonPath traversal. At large scale (evaluating hundreds of keys),
 * this creates excessive GC pressure and allocation storms.
 *
 * The fix must ensure:
 * 1. Scalar path reads (string, number, boolean) still work correctly
 * 2. Array path reads via arr.* functions still work correctly
 * 3. Null / missing path handling is preserved
 * 4. The entire evaluation stays within the JsonNode type system — no round-tripping
 *    through raw Java POJOs (Object, Map, List) and back
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

    // -------------------------------------------------------------------------
    // Scalar path reads — these must work correctly after any provider change
    // -------------------------------------------------------------------------

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
                // string equality
                Arguments.of("{\"name\": \"hope\"}", "\"$.name\" == \"hope\"", true),
                Arguments.of("{\"name\": \"hope\"}", "\"$.name\" == \"other\"", false),
                // numeric equality and comparison
                Arguments.of("{\"count\": 42}", "\"$.count\" == 42", true),
                Arguments.of("{\"count\": 42}", "\"$.count\" > 40", true),
                Arguments.of("{\"count\": 42}", "\"$.count\" < 40", false),
                // boolean
                Arguments.of("{\"active\": true}", "\"$.active\" == true", true),
                Arguments.of("{\"active\": false}", "\"$.active\" == false", true),
                // nested path
                Arguments.of("{\"a\": {\"b\": \"deep\"}}", "\"$.a.b\" == \"deep\"", true),
                // missing path gracefully returns false via InjectValueErrorHandlingStrategy
                Arguments.of("{\"x\": 1}", "\"$.missing\" == \"value\"", false),
                // null-valued field
                Arguments.of("{\"field\": null}", "\"$.field\" == \"something\"", false)
        );
    }

    // -------------------------------------------------------------------------
    // arr.in — needle in haystack array
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrInWithJsonPathHaystack() {
        val node = mapper.readTree("{\"haystack\": [1, 2, 3, 4, 8, 16], \"needle\": 2}");
        assertTrue(evaluator.evaluate(parse("arr.in(\"$.needle\", \"$.haystack\") == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrInNeedleNotInHaystack() {
        val node = mapper.readTree("{\"haystack\": [1, 2, 3, 4, 8, 16], \"needle\": 99}");
        assertFalse(evaluator.evaluate(parse("arr.in(\"$.needle\", \"$.haystack\") == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrInStringNeedle() {
        val node = mapper.readTree("{\"haystack\": [\"alpha\", \"beta\", \"gamma\"], \"needle\": \"beta\"}");
        assertTrue(evaluator.evaluate(parse("arr.in(\"$.needle\", \"$.haystack\") == true"), node));
    }

    // -------------------------------------------------------------------------
    // arr.contains_any — at least one element of rhs is in lhs
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrContainsAny() {
        val node = mapper.readTree("{\"val\": [1, 2, 4, 8, 16]}");
        assertTrue(evaluator.evaluate(parse("arr.contains_any(\"$.val\", [2, 3]) == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrContainsAnyNoMatch() {
        val node = mapper.readTree("{\"val\": [1, 2, 4, 8, 16]}");
        assertFalse(evaluator.evaluate(parse("arr.contains_any(\"$.val\", [9, 7]) == true"), node));
    }

    // -------------------------------------------------------------------------
    // arr.contains_all — all elements of rhs must be in lhs
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrContainsAll() {
        val node = mapper.readTree("{\"val\": [1, 2, 4, 8, 16]}");
        assertTrue(evaluator.evaluate(parse("arr.contains_all(\"$.val\", [2, 4]) == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrContainsAllPartialMatch() {
        val node = mapper.readTree("{\"val\": [1, 2, 4, 8, 16]}");
        assertFalse(evaluator.evaluate(parse("arr.contains_all(\"$.val\", [2, 3]) == true"), node));
    }

    // -------------------------------------------------------------------------
    // arr.is_empty
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrIsEmptyTrue() {
        val node = mapper.readTree("{\"empty\": [], \"nonempty\": [1, 2, 3]}");
        assertTrue(evaluator.evaluate(parse("arr.is_empty(\"$.empty\") == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrIsEmptyFalse() {
        val node = mapper.readTree("{\"empty\": [], \"nonempty\": [1, 2, 3]}");
        assertFalse(evaluator.evaluate(parse("arr.is_empty(\"$.nonempty\") == true"), node));
    }

    // -------------------------------------------------------------------------
    // arr.len
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrLength() {
        val node = mapper.readTree("{\"items\": [10, 20, 30, 40, 50]}");
        assertTrue(evaluator.evaluate(parse("arr.len(\"$.items\") == 5"), node));
    }

    @Test
    @SneakyThrows
    void testArrLengthEmpty() {
        val node = mapper.readTree("{\"items\": []}");
        assertTrue(evaluator.evaluate(parse("arr.len(\"$.items\") == 0"), node));
    }

    // -------------------------------------------------------------------------
    // arr.not_in — needle NOT in haystack array
    // -------------------------------------------------------------------------

    @Test
    @SneakyThrows
    void testArrNotInWhenAbsent() {
        val node = mapper.readTree("{\"haystack\": [1, 2, 3], \"needle\": 99}");
        assertTrue(evaluator.evaluate(parse("arr.not_in(\"$.needle\", \"$.haystack\") == true"), node));
    }

    @Test
    @SneakyThrows
    void testArrNotInWhenPresent() {
        val node = mapper.readTree("{\"haystack\": [1, 2, 3], \"needle\": 2}");
        assertFalse(evaluator.evaluate(parse("arr.not_in(\"$.needle\", \"$.haystack\") == true"), node));
    }

    // -------------------------------------------------------------------------
    // Bulk evaluation — ensure evaluate(List, JsonNode) also works correctly
    // (this is the hot path in production)
    // -------------------------------------------------------------------------

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
        assertTrue(results.get(0), "$.value == 20");
        assertTrue(results.get(1), "$.string == Hello");
        assertTrue(results.get(2), "$.flag == true");
        assertTrue(results.get(3), "arr.in string in list");
        assertTrue(results.get(4), "arr.contains_any");
        assertTrue(results.get(5), "arr.len == 3");
        assertTrue(results.get(6), "arr.not_in");
    }

    /**
     * Regression test: evaluateFirst must return the correct first matching index
     * across a mix of scalar and array JsonPath expressions.
     */
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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @SneakyThrows
    private io.appform.hope.core.Evaluatable parse(String rule) {
        return new HopeParser(new StringReader(rule)).parse(functionRegistry);
    }
}
