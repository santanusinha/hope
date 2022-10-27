package io.appform.hope.lang;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class BenchmarkTest {

    public static final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testBenchmark() throws RunnerException {
        val opt = new OptionsBuilder()
                .include(String.format("%s.*", this.getClass().getName()))
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .warmupTime(TimeValue.seconds(5))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(5))
                .measurementIterations(3)
                .threads(1)
                .forks(3)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();
        val results = new Runner(opt).run();
        results.iterator()
                .forEachRemaining(new Consumer<>() {
                    @SneakyThrows
                    @Override
                    public void accept(RunResult runResult) {
                        val benchmarkName = runResult.getParams().getBenchmark();
                        val outputFilePath = Paths.get(String.format("perf/results/%s.json", benchmarkName));
                        val outputNode = mapper.createObjectNode();
                        outputNode.put("name", benchmarkName);
                        outputNode.put("mode", runResult.getParams().getMode().name());
                        outputNode.put("count", runResult.getParams().getMeasurement().getCount());
                        outputNode.put("threads", runResult.getParams().getThreads());
                        outputNode.put("forks", runResult.getParams().getForks());
                        outputNode.put("mean_ops", runResult.getPrimaryResult().getStatistics().getMean());
                        Files.write(outputFilePath, mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(outputNode));
                    }
                });
    }


}
