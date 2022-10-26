///*
// * Copyright 2022. Santanu Sinha
// *
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
// * compliance with the License. You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is
// * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// * express or implied. See the License for the specific language governing permissions and limitations
// * under the License.
// */
//
//package io.appform.hope.lang;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.appform.hope.core.Evaluatable;
//import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.provider.Arguments;
//import org.openjdk.jmh.annotations.Benchmark;
//import org.openjdk.jmh.annotations.Level;
//import org.openjdk.jmh.annotations.Mode;
//import org.openjdk.jmh.annotations.Param;
//import org.openjdk.jmh.annotations.Scope;
//import org.openjdk.jmh.annotations.Setup;
//import org.openjdk.jmh.annotations.State;
//import org.openjdk.jmh.infra.Blackhole;
//import org.openjdk.jmh.results.RunResult;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;
//import org.openjdk.jmh.runner.options.TimeValue;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
///**
// * Test performance between different constructs
// */
//@Slf4j
//public class PerfTest {
//
//    public static final ObjectMapper mapper = new ObjectMapper();
//
//    public PerfTest() {
//    }
//
//    @State(Scope.Benchmark)
//    public static class BenchmarkState {
//        @Param({
//                "\"/value\" > 11" +
//                        " && \"/value\" <30" +
//                        " && \"/value\" > 19 && \"/value\" < 21" +
//                        " && \"/value\" >=20 && \"/value\" < 21" +
//                        " && \"/value\" > 11 && \"/value\" <= 20",
//                "'/value' > 11" +
//                        " && '/value' <30" +
//                        " && '/value' > 19 && '/value' < 21" +
//                        " && '/value' >=20 && '/value' < 21" +
//                        " && '/value' > 11 && '/value' <= 20",
////                "\"$.value\" > 11" +
////                        " && \"$.value\" <30" +
////                        " && \"$.value\" > 19 && \"$.value\" < 21" +
////                        " && \"$.value\" >=20 && \"$.value\" < 21" +
////                        " && \"$.value\" > 11 && \"$.value\" <= 20",
////                "'$.value' > 11" +
////                        " && '$.value' <30" +
////                        " && '$.value' > 19 && '$.value' < 21" +
////                        " && '$.value' >=20 && '$.value' < 21" +
////                        " && '$.value' > 11 && '$.value' <= 20"
//        })
//        public String payload;
//        private JsonNode contextNode;
//        private Evaluatable rule;
//        private HopeLangEngine hopeLangEngine;
//
//        @Setup(Level.Trial)
//        public void setUp() throws JsonProcessingException {
//            hopeLangEngine = HopeLangEngine.builder()
//                    .build();
//            rule = hopeLangEngine.parse(payload);
//            contextNode = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
//        }
//    }
//
//    @Test
//    public void launchBenchmark() throws Exception {
//        Options opt = new OptionsBuilder()
//                .include(this.getClass().getName() + ".testPerf")
//                .mode(Mode.Throughput)
//                .timeUnit(TimeUnit.SECONDS)
//                .warmupTime(TimeValue.seconds(2))
//                .warmupIterations(2)
//                .measurementTime(TimeValue.seconds(10))
//                .measurementIterations(1)
//                .threads(2)
//                .forks(0)
//                .shouldFailOnError(true)
//                .shouldDoGC(true)
//                .build();
//        new Runner(opt).run();
//    }
//
//    @SneakyThrows
//    @Benchmark
//    public boolean testPerf(BenchmarkState state) {
//        return state.hopeLangEngine.evaluate(state.rule, state.contextNode);
//    }
//
//    @Test
//    public void testPerfJsonPointer() throws Exception {
//        Options opt = new OptionsBuilder()
//                .include(this.getClass().getName() + ".testPerfJsonPointer*")
//                .mode(Mode.Throughput)
//                .timeUnit(TimeUnit.SECONDS)
//                .warmupTime(TimeValue.seconds(2))
//                .warmupIterations(2)
//                .measurementTime(TimeValue.seconds(5))
//                .measurementIterations(5)
//                .threads(2)
//                .forks(0)
//                .shouldFailOnError(true)
//                .shouldDoGC(true)
//                .build();
//        Collection<RunResult> run = new Runner(opt).run();
//        System.out.println(mapper.writeValueAsString(run.iterator().next().getPrimaryResult()));
//    }
//
//    @Test
//    public void testPerfJsonPath() throws Exception {
//        Options opt = new OptionsBuilder()
//                .include(this.getClass().getName() + ".testPerfJsonPath*")
//                .mode(Mode.Throughput)
//                .timeUnit(TimeUnit.SECONDS)
//                .warmupTime(TimeValue.seconds(2))
//                .warmupIterations(2)
//                .measurementTime(TimeValue.seconds(5))
//                .measurementIterations(5)
//                .threads(2)
//                .forks(0)
//                .shouldFailOnError(true)
//                .shouldDoGC(true)
//                .build();
//        Collection<RunResult> run = new Runner(opt).run();
//        System.out.println(mapper.writeValueAsString(run.iterator().next().getPrimaryResult()));
//    }
//
//    @State(Scope.Benchmark)
//    public static class JsonPathState {
//        public List<Evaluatable> rules;
//        private JsonNode contextNode;
//        private HopeLangEngine hopeLangEngine;
//
//        @Setup(Level.Trial)
//        public void setUp() throws IOException {
//            hopeLangEngine = HopeLangEngine.builder()
//                    .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
//                    .build();
//            List<String> hopeRules = readAllHopeRulesForJsonPath();
//            rules = hopeRules.stream()
//                    .map(rule -> hopeLangEngine.parse(rule))
//                    .collect(Collectors.toList());
//            contextNode = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");
//        }
//
//        private List<String> readAllHopeRulesForJsonPointer() throws IOException {
//            return Files.readAllLines(Paths.get("src/test/resources/hope_rules_jsonpointer.txt"));
//        }
//
//        private List<String> readAllHopeRulesForJsonPath() throws IOException {
//            return Files.readAllLines(Paths.get("src/test/resources/hope_rules_jsonpath.txt"));
//        }
//    }
//
//    @SneakyThrows
//    @Benchmark
//    public void testPerfJsonPointer(Blackhole blackhole, BulkBenchmarkStateV2 state) {
//        state.rules
//                .forEach(evaluatable ->
//                        blackhole.consume(state.hopeLangEngine.evaluate(evaluatable, state.contextNode)));
//    }
//
//    @SneakyThrows
//    @Benchmark
//    public void testBulkPerfPointer(Blackhole blackhole, BulkBenchmarkStateV2 state) {
//        blackhole.consume(state.hopeLangEngine.evaluateAll(state.rules, state.contextNode));
//    }
//
//    private static Stream<Arguments> rules() {
//        return Stream.of(
//                Arguments.of("\"/value\" > 11" +
//                        " && \"/value\" <30" +
//                        " && \"/value\" > 19 && \"/value\" < 21" +
//                        " && \"/value\" >=20 && \"/value\" < 21" +
//                        " && \"/value\" > 11 && \"/value\" <= 20"),
//                Arguments.of(),
//                Arguments.of(),
//                Arguments.of()
//        );
//    }
//
//}