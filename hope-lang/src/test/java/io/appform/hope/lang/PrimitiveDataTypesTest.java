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
import com.fasterxml.jackson.databind.node.NullNode;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.lang.parser.HopeParser;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;

import java.io.StringReader;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests base data types: number, boolean, json path, pointer, strings
 */
class PrimitiveDataTypesTest {
    final JsonNode node = NullNode.getInstance();
    final FunctionRegistry functionRegistry;

    PrimitiveDataTypesTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @Test
    void testInt() throws Exception {
        val parser = new HopeParser(new StringReader("1"));
        val operand = parser.NumericRepr();
        assertEquals(1.0, operand.getValue());
    }

    @Test
    void testDouble() throws Exception {
        val parser = new HopeParser(new StringReader("1.5"));
        val operand = parser.NumericRepr();
        assertEquals(1.5, ((NumericValue) operand).getValue());
    }

    @Test
    void testBoolean() throws Exception {
        val parser = new HopeParser(new StringReader("true"));
        val operand = parser.BooleanRepr();
        assertTrue(((BooleanValue) operand).getValue());
    }

    @Test
    void testBooleanFalse() throws Exception {
        val parser = new HopeParser(new StringReader("false"));
        val operand = parser.BooleanRepr();
        assertFalse(((BooleanValue) operand).getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"abc\"",
            "'abc'"
    })
    void testString(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.StringRepr();
        assertEquals("abc", operand.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"$.abc\"",
            "'$.abc'"
    })
    void testPath(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.JsonPathRepr();
        assertEquals("$.abc", operand.getPath());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"/abc\"",
            "'/abc'"
    })
    void testPointer(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.JsonPointerRepr();
        assertEquals("/abc", operand.getPointer());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"'abc'\"",
//            "'\'abc\''"
    })
    void testSQString(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.StringRepr();
        assertEquals("'abc'", operand.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
//            "\"\"abc\"\"",
            "'\"abc\"'"
    })
    void testDQString(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.StringRepr();
        assertEquals("\"abc\"", operand.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "''abc''"
    })
    void testStringDQParsingBug(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.StringRepr();
        assertThrows(AssertionFailedError.class, () -> assertEquals("'abc'", operand.getValue()));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "\"\"abc\"\""
    })
    void testStringSQParsingBug(final String rule) throws Exception {
        val parser = new HopeParser(new StringReader(rule));
        val operand = parser.StringRepr();
        assertThrows(AssertionFailedError.class, () -> assertEquals("\"abc\"", operand.getValue()));
    }
}
