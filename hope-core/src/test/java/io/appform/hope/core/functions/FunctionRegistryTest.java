package io.appform.hope.core.functions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

class FunctionRegistryTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testDiscovery(boolean autoDiscoveryEnabled) {
        FunctionRegistry functionRegistry = new FunctionRegistry();
        //only adding function1 package but on autoDiscoveryEnabledFunction2 should also be discovered
        functionRegistry.discover(List.of("io.appform.hope.core.functions.testFunction1"), autoDiscoveryEnabled);

        Optional<FunctionRegistry.FunctionMeta> testFunction1 = functionRegistry.find("testFunction1");
        Optional<FunctionRegistry.FunctionMeta> testFunction2 = functionRegistry.find("testFunction2");

        Assertions.assertTrue(testFunction1.isPresent());

        if (autoDiscoveryEnabled) {
            Assertions.assertTrue(testFunction2.isPresent());
        } else {
            Assertions.assertFalse(testFunction2.isPresent());
        }
    }
}
