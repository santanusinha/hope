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

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A global registry of functions provided by library as well as registered by user.
 */
@Slf4j
public class FunctionRegistry {

    public static final String DEFAULT_FUNCTIONS_PACKAGE = "io.appform.hope.core.functions.impl";

    @Data
    @Builder
    public static class ConstructorMeta {
        private final List<Class<?>> paramTypes;
        private final boolean hasVariableArgs;
        private final Constructor<? extends HopeFunction> constructor;
    }


    @Data
    @Builder
    public static class FunctionMeta {
        private final List<ConstructorMeta> constructors;
    }

    private final Map<String, FunctionMeta> knownFunctions = new HashMap<>();
    private volatile boolean discoveredAlready = false;

    /**
     * Discover and register {@link HopeFunction} implementations that are annotated with {@link FunctionImplementation}.
     *
     * @param packages Extra packages to be scanned besides the standard library.
     */
    public synchronized void discover(List<String> packages, boolean autoFunctionDiscoveryEnabled) {
        if (discoveredAlready) {
            return;
        }
        final List<URL> packageUrls = new ImmutableList.Builder<URL>()
                .addAll(ClasspathHelper.forPackage(DEFAULT_FUNCTIONS_PACKAGE))
                .addAll(packages.stream()
                                .flatMap(packagePath -> ClasspathHelper.forPackage(packagePath)
                                        .stream())
                                .toList())
                .build();
        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setUrls(packageUrls)
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner());

        if (!autoFunctionDiscoveryEnabled) {
            final FilterBuilder filter = new FilterBuilder();
            filter.includePackage(DEFAULT_FUNCTIONS_PACKAGE);
            packages.forEach(filter::includePackage);
            configurationBuilder.filterInputsBy(filter);
        }
        Reflections reflections = new Reflections(configurationBuilder);
        log.debug("Type scanning complete");
        final Set<Class<? extends HopeFunction>> classes = reflections.getSubTypesOf(HopeFunction.class);
        classes
                .stream()
                .filter(type -> type.getAnnotation(FunctionImplementation.class) != null)
                .forEach(this::register);
        discoveredAlready = true;
    }

    public synchronized void discover(List<String> packages) {
        discover(packages, true);
    }

    /**
     * Register a {@link HopeFunction} implementation. Needs to be annotated with {@link FunctionImplementation}.
     *
     * @param clazz Function class.
     */
    public void register(Class<? extends HopeFunction> clazz) {
        final FunctionImplementation annotation = clazz.getAnnotation(FunctionImplementation.class);
        Preconditions.checkNotNull(annotation,
                                   clazz.getSimpleName() + " is not annotated with FucntionImplementation");
        final List<ConstructorMeta> constructors = constructors(clazz);
        final String functionName = annotation.value();
        Preconditions.checkArgument(!knownFunctions.containsKey(functionName),
                                    "Function '" + functionName + "' is already registered");
        Preconditions.checkArgument(constructors.stream()
                                            .noneMatch(ConstructorMeta::isHasVariableArgs)
                                             || constructors.size() == 1,
                                    "Illegal declaration for: " + functionName +"." +
                                            "Functions with variant args can have only one implementation." +
                                            " Overloads are not allowed");
        Preconditions.checkArgument(constructors.stream()
                                   .distinct()
                                   .count() == constructors.size(),
                                    "Function " + functionName + " has multiple constructors (overloads) " +
                                            "with same number of params. This means it cannot be resolved at runtime.");
        knownFunctions.put(
                functionName,
                FunctionMeta.builder()
                        .constructors(constructors)
                        .build());
        log.debug("Registered function: {}", functionName);
    }

    /**
     * Find a {@link HopeFunction} implementation by name.
     *
     * @param name Name for the function to find
     * @return Meta data for the function if found or null.
     */
    public Optional<FunctionMeta> find(String name) {
        return Optional.ofNullable(knownFunctions.getOrDefault(name, null));
    }

    @SuppressWarnings("unchecked")
    private static List<ConstructorMeta> constructors(Class<? extends HopeFunction> type) {
        final Constructor<? extends HopeFunction>[] declaredConstructors
                = (Constructor<? extends HopeFunction>[]) type.getDeclaredConstructors();
        FunctionImplementation annotation = type.getAnnotation(FunctionImplementation.class);
        return Arrays.stream(declaredConstructors)
                .map(declaredConstructor -> {
                    final Class<?>[] declaredParamTypes = declaredConstructor
                            .getParameterTypes();
                    final List<Class<?>> paramTypes = Arrays.stream(declaredParamTypes)
                            .filter(parameterType -> parameterType.isArray()
                                    ? parameterType.getComponentType()
                                    .isAssignableFrom(Value.class)
                                    : parameterType.isAssignableFrom(Value.class))
                            .toList();
                    Preconditions.checkArgument(
                            paramTypes.size() == declaredParamTypes.length,
                            "Non value parameter types declared for constructor in function '"
                                    + annotation.value() + "'. Param types: " + Arrays.stream(declaredParamTypes)
                                    .map(Class::getSimpleName)
                                    .toList());
                    final boolean variantArgs = paramTypes.stream()
                            .anyMatch(Class::isArray);
                    Preconditions.checkArgument(!variantArgs || paramTypes.size() == 1,
                                                "For (Value... ) vararg func only one param is allowed.");

                    return ConstructorMeta.builder()
                            .paramTypes(paramTypes)
                            .hasVariableArgs(variantArgs)
                            .constructor(declaredConstructor)
                            .build();
                })
                .collect(Collectors.toList());

    }
}
