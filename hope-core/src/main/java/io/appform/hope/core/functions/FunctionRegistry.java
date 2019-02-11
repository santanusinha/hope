package io.appform.hope.core.functions;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.appform.hope.core.Value;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
@Slf4j
public class FunctionRegistry {

    @Data
    @Builder
    public static class FunctionMeta {
        private final List<Class<? extends Value>> paramTypes;
        private final Class<? extends HopeFunction> functionClass;
    }

    private static final Map<String, FunctionMeta> knownFunctions = new HashMap<>();

    public static void discover() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("io.appform.hope.core.functions.impl"))
                        .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                        .filterInputsBy(new FilterBuilder().includePackage("io.appform.hope.core.functions.impl")));
        log.info("Type scanning complete");
        reflections.getTypesAnnotatedWith(FunctionImplementation.class)
                .forEach(type -> {
                    final FunctionImplementation annotation = type.getAnnotation(FunctionImplementation.class);
                    knownFunctions.put(
                            annotation.value(),
                            FunctionMeta.builder()
                                    .paramTypes(ImmutableList.copyOf(annotation.paramTypes()))
                                    .functionClass((Class<? extends HopeFunction>) type)
                            .build());
                });
    }

    public static void register(Class<? extends HopeFunction> clazz) {
        final FunctionImplementation annotation = clazz.getAnnotation(FunctionImplementation.class);
        Preconditions.checkNotNull("annotation",
                                   clazz.getSimpleName() + " is not annotated with FucntionImplementation");
        knownFunctions.put(
                annotation.value(),
                FunctionMeta.builder()
                        .paramTypes(ImmutableList.copyOf(annotation.paramTypes()))
                        .functionClass(clazz)
                        .build());
    }

    public static Optional<FunctionMeta> find(String name) {
        return Optional.ofNullable(knownFunctions.getOrDefault(name, null));
    }
}
