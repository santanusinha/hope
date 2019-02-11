package io.appform.hope.core.functions;

import com.google.common.base.Preconditions;
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

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
public class FunctionRegistry {

    @Data
    @Builder
    public static class FunctionMeta {
        private final List<Class<?>> paramTypes;
        private final boolean arrayValue;
        private final Class<? extends HopeFunction> functionClass;
    }

    private static final Map<String, FunctionMeta> knownFunctions = new HashMap<>();

    static {
        discover();
    }

    public static void discover() {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("io.appform.hope.core.functions.impl"))
                        .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                        .filterInputsBy(new FilterBuilder().includePackage("io.appform.hope.core.functions.impl")));
        log.info("Type scanning complete");
        final Set<Class<? extends HopeFunction>> classes = reflections.getSubTypesOf(HopeFunction.class);
        classes
                .stream()
                .filter(type -> type.getAnnotation(FunctionImplementation.class) != null)
                .forEach(FunctionRegistry::register);
    }

    public static void register(Class<? extends HopeFunction> clazz) {
        final FunctionImplementation annotation = clazz.getAnnotation(FunctionImplementation.class);
        Preconditions.checkNotNull("annotation",
                                   clazz.getSimpleName() + " is not annotated with FucntionImplementation");
        final List<Class<?>> paramTypes = paramTypes(clazz);
        final boolean arrayValue = paramTypes.stream()
                .anyMatch(Class::isArray);
        Preconditions.checkArgument(!arrayValue || paramTypes.size() == 1,
                                    "For (Value... ) vararg func only one param is allowed.");
        knownFunctions.put(
                annotation.value(),
                FunctionMeta.builder()
                        .paramTypes(paramTypes)
                        .arrayValue(arrayValue)
                        .functionClass(clazz)
                        .build());
        log.debug("Registered function: {}", annotation.value());
    }

    public static Optional<FunctionMeta> find(String name) {
        return Optional.ofNullable(knownFunctions.getOrDefault(name, null));
    }

    private static List<Class<?>> paramTypes(Class<? extends HopeFunction> type) {
        final Constructor<?>[] declaredConstructors = type.getDeclaredConstructors();
        FunctionImplementation annotation = type.getAnnotation(FunctionImplementation.class);
        Preconditions.checkArgument(
                declaredConstructors != null && declaredConstructors.length == 1,
                "Function " + annotation.value() + " must have only one constructor");
        final Class<?>[] declaredParamTypes = declaredConstructors[0]
                .getParameterTypes();
        final List<Class<?>> paramTypes = Arrays.stream(declaredParamTypes)
                .filter(parameterType -> parameterType.isArray()
                        ? parameterType.getComponentType().isAssignableFrom(Value.class)
                        : parameterType.isAssignableFrom(Value.class))
                .collect(Collectors.toList());
        Preconditions.checkArgument(
                paramTypes.size() == declaredParamTypes.length,
                "Non value parameter types declared for constructor in function '"
                        + annotation.value() + "'. Param types: " + Arrays.stream(declaredParamTypes)
                        .map(Class::getSimpleName)
                        .collect(Collectors.toList()));
        return paramTypes;
    }
}
