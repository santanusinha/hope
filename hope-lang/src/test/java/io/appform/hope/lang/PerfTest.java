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
import com.google.common.base.Stopwatch;
import io.appform.hope.core.functions.FunctionRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test performance between different constructs
 */
@Slf4j
class PerfTest {

    final ObjectMapper mapper = new ObjectMapper();

    final JsonNode node = NullNode.getInstance();
    final FunctionRegistry functionRegistry;

    PerfTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("rules")
    @SneakyThrows
    void testPerf(final String payload) {
        val node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");

        final HopeLangEngine hopeLangParser = HopeLangEngine.builder()
                .build();

        val rule = hopeLangParser.parse(payload);

        IntStream.range(0, 10)
                .forEach(times -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    IntStream.range(1, 1_000_000)
                            .forEach(i -> hopeLangParser.evaluate(rule, node));
                    log.info("Time taken for one million evals: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                });
        assertTrue(true);
    }

    private static Stream<Arguments> rules() {
        return Stream.of(
                Arguments.of("\"/value\" > 11" +
                        " && \"/value\" <30" +
                        " && \"/value\" > 19 && \"/value\" < 21" +
                        " && \"/value\" >=20 && \"/value\" < 21" +
                        " && \"/value\" > 11 && \"/value\" <= 20"),
                Arguments.of("\"$.value\" > 11" +
                        " && \"$.value\" <30" +
                        " && \"$.value\" > 19 && \"$.value\" < 21" +
                        " && \"$.value\" >=20 && \"$.value\" < 21" +
                        " && \"$.value\" > 11 && \"$.value\" <= 20"),
                Arguments.of("'/value' > 11" +
                        " && '/value' <30" +
                        " && '/value' > 19 && '/value' < 21" +
                        " && '/value' >=20 && '/value' < 21" +
                        " && '/value' > 11 && '/value' <= 20"),
                Arguments.of("'$.value' > 11" +
                        " && '$.value' <30" +
                        " && '$.value' > 19 && '$.value' < 21" +
                        " && '$.value' >=20 && '$.value' < 21" +
                        " && '$.value' > 11 && '$.value' <= 20")
        );
    }
}