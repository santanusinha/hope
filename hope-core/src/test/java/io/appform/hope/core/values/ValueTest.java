package io.appform.hope.core.values;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueTest {

    @Test
    void BooleanValue_Equals() {
        assertEquals(new BooleanValue(true), new BooleanValue(true));
        assertEquals(new BooleanValue(false), new BooleanValue(false));
        assertEquals(new BooleanValue(new JsonPathValue("providerId")), new BooleanValue(new JsonPathValue("providerId")));
        assertEquals(new BooleanValue(new JsonPointerValue("/providerId")), new BooleanValue(new JsonPointerValue("/providerId")));
    }

    @Test
    void NumericValue_Equals() {
        assertEquals(new NumericValue(12), new NumericValue(12));
        assertEquals(new NumericValue(343), new NumericValue(343));
        assertEquals(new NumericValue(new JsonPathValue("providerId")), new NumericValue(new JsonPathValue("providerId")));
        assertEquals(new NumericValue(new JsonPointerValue("/providerId")), new NumericValue(new JsonPointerValue("/providerId")));
    }

    @Test
    void StringValue_Equals() {
        assertEquals(new StringValue("12"), new StringValue("12"));
        assertEquals(new StringValue("343"), new StringValue("343"));
        assertEquals(new StringValue(new JsonPathValue("providerId")), new StringValue(new JsonPathValue("providerId")));
        assertEquals(new StringValue(new JsonPointerValue("/providerId")), new StringValue(new JsonPointerValue("/providerId")));
    }

    @Test
    void JsonPathValue_Equals() {
        assertEquals(new JsonPathValue("bankId"), new JsonPathValue("bankId"));
        assertEquals(new JsonPathValue("providerRole"), new JsonPathValue("providerRole"));
    }
}