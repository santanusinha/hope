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
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test performance between different constructs
 */
@Slf4j
public class JsonPathPerfTest extends BenchmarkTest {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private List<Evaluatable> rules;
        private JsonNode contextNode;
        private HopeLangEngine hopeLangEngine;

        @Setup(Level.Trial)
        public void setUp() throws IOException {
            hopeLangEngine = HopeLangEngine.builder()
                    .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
                    .build();
            List<String> hopeRules = readAllHopeRulesForJsonPath();
            rules = hopeRules.stream()
                    .map(rule -> hopeLangEngine.parse(rule))
                    .collect(Collectors.toList());
            contextNode = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
        }

        private List<String> readAllHopeRulesForJsonPath() throws IOException {
            return Files.readAllLines(Paths.get("src/test/resources/samples/jsonpath.txt"));
        }
    }

    @SneakyThrows
    @Benchmark
    public void testSingleEval(Blackhole blackhole, BenchmarkState state) {
        state.rules
                .forEach(evaluatable ->
                        blackhole.consume(state.hopeLangEngine.evaluate(evaluatable, state.contextNode)));
    }

    @SneakyThrows
    @Benchmark
    public void testBulkEval(Blackhole blackhole, BenchmarkState state) {
        blackhole.consume(state.hopeLangEngine.evaluate(state.rules, state.contextNode));
    }
}