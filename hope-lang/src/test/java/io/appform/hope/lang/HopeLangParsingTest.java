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

package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.google.common.base.Stopwatch;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.Value;
import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import io.appform.hope.core.exceptions.impl.HopeMissingValueError;
import io.appform.hope.core.exceptions.impl.HopeTypeMismatchError;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.values.*;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests hope lang parsing
 */
@Slf4j
class HopeLangParsingTest {
    final JsonNode node = NullNode.getInstance();
    final FunctionRegistry functionRegistry;

    HopeLangParsingTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @Test
    void testInt() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1"));
        final Value operand = parser.NumericRepr();
        assertEquals(1.0, NumericValue.class.cast(operand).getValue());
    }

    @Test
    void testDouble() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1.5"));
        final Value operand = parser.NumericRepr();
        assertEquals(1.5, NumericValue.class.cast(operand).getValue());
    }

    @Test
    void testBoolean() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true"));
        final Value operand = parser.BooleanRepr();
        assertTrue(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    void testBooleanFalse() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false"));
        final Value operand = parser.BooleanRepr();
        assertFalse(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    void testString() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"abc\""));
        final Value operand = parser.StringRepr();
        assertEquals("abc", StringValue.class.cast(operand).getValue());
    }

    @Test
    void testPath() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"$.abc\""));
        final Value operand = parser.JsonPathRepr();
        assertEquals("$.abc", JsonPathValue.class.cast(operand).getPath());
    }

    @Test
    void testPointer() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"/abc\""));
        final Value operand = parser.JsonPointerRepr();
        assertEquals("/abc", JsonPointerValue.class.cast(operand).getPointer());
    }

    @Test
    void testEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 23"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 29"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 29"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 23"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testAndSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & true"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testAndFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testOrSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | true"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testOrSuccess2() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testOrFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false | false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testGreaterSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 > 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testGreaterFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 > 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testGreaterEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testGreaterEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 >= 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testLesserSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 < 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testLesserFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 < 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testLesserEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 <= 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testLessEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 <= 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testLessEqualsFailParenthesis() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 <= 2)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testAndCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 && 2 < 5"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testAndCombinerFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) && (2 < 5)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testOrCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 || 2 < 5"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testOrCombinerSuccessRhs() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) || (2 < 5)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testCombinerComplexSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("((3 >= 2) && (2 < 5)) || 2 == 2 "));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testCombinerComplexFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 >= 2) && (2 == 5)) && 2 == 2)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        System.out.println(operator);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testCombinerComplexSuccessBothSides() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 <= 2) || (2 == 5)) && ((2 == 2) ||  (2 == 1)))"));
        final Evaluatable operator = parser.parse(functionRegistry);
        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"name\" : \"santanu\" }");

        HopeParser parser = new HopeParser(new StringReader("\"$.name\" == \"santanu\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"name\" : \"santanu\" }");

        HopeParser parser = new HopeParser(new StringReader("\"/name\" == \"santanu\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumeric() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" == 93"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumeric() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("\"/count\" == 93"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericGreaterRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("94 > \"$.count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericGreaterRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("94 > \"/count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericGreaterLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" > 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericGreaterLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"/count\" > 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericGreaterBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 97, \"rhs\" : 94 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" > \"$.rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericGreaterBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 97, \"rhs\" : 94 }");

        HopeParser parser = new HopeParser(new StringReader("\"/lhs\" > \"/rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathPointerNumericGreaterBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 97, \"rhs\" : 94 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" > \"/rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericLesserRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("92 < \"$.count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericLesserRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("92 < \"/count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericLesserLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 91 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" < 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericLesserLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 91 }");

        HopeParser parser = new HopeParser(new StringReader("\"/count\" < 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathNumericLesserBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 94, \"rhs\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" < \"$.rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerNumericLesserBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 94, \"rhs\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"/lhs\" < \"/rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerPathNumericLesserBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 94, \"rhs\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" < \"/rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotSimple() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^false"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotSimplePath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^\"$.boolValue\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotSimplePointer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^\"/boolValue\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotSimplePathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotSimplePointerParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("(^\"/boolValue\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotCombinerPathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num\" : 43 }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && \"$.num\" > 40)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }


    @Test
    void testNotCombinerPointerParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num\" : 43 }");

        HopeParser parser = new HopeParser(new StringReader("(^\"/boolValue\" && \"/num\" > 40)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotCombinerPathParenthComplex() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && (\"$.num1\" > 45 || \"$.num2\" < 50))"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testNotCombinerPointerParenthComplex() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}");

        HopeParser parser = new HopeParser(new StringReader("(^\"/boolValue\" && (\"/num1\" > 45 || \"/num2\" < 50))"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathAbs() throws Exception {
        //FunctionRegistry.discover();
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("93 <= math.abs(\"$.count\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathAbs() throws Exception {
        //FunctionRegistry.discover();
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("93 <= math.abs(\"/count\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathAdd() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= math.add(\"$.a\", \"$.b\", 7)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathAdd() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= math.add(\"/a\", \"/b\", 7)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncSysEpoch() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= sys.epoch()"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncUtilsHashJ() throws Exception {
        System.out.println("Hash" + new Integer(-23).hashCode());
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.abs(utils.hash_j(-23)) == math.abs(-1070137344)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncUtilsHashM128() throws Exception {
        System.out.println("Hash" + new Integer(-23).hashCode());
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.abs(utils.hash_m128(-23)) == 2940310638642342768"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathAddLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(\"$.a\", \"$.b\", 7) == 12"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathAddLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(\"/a\", \"/b\", 7) == 12"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathAddNested() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(math.add(\"$.a\", \"$.b\", 7), \"$.c\", 4) == 25"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathAddNested() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(math.add(\"/a\", \"/b\", 7), \"/c\", 4) == 25"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathProdLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.prod(\"$.a\", \"$.b\", 7) == 42"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathProdLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.prod(\"/a\", \"/b\", 7) == 42"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPathFuncMathNestedLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(math.sub(math.add(\"$.a\", \"$.b\", 7), 13)) == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testJsonPointerFuncMathNestedLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(math.sub(math.add(\"/a\", \"/b\", 7), 13)) == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncSysEpochLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("sys.epoch() >= 7"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLower() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"ABC\" }");

        HopeParser parser = new HopeParser(new StringReader("str.lower(\"$.val\") == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLowerJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"ABC\" }");

        HopeParser parser = new HopeParser(new StringReader("str.lower(\"/val\") == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrUpper() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"$.val\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrUpperJptr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"/val\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrUpperSnakeCase() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val_txt\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"$.val_txt\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrUpperSnakeCaseJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val_txt\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"/val_txt\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLen() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLenJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"/val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrMatch() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.match(\"^a.*\", \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrMatchJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.match(\"^a.*\", \"/val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrSubStr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"$.val\", 0, 3) == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrSubStrJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"/val\", 0, 3) == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrSubStrNoEndOverload() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"$.val\", 3) == \"def\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrSubStrNoEndOverloadJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"/val\", 3) == \"def\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncPathExists() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("path.exists(\"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncPathExistsJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("pointer.exists(\"/val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAny() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any(\"$.val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAnyJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any(\"/val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAnyNeg() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any([9,7], \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAnyNegJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any([9,7], \"/val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAll() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,3, 4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all(\"$.val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAllJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,3, 4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all(\"/val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAllNeg() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all([1, 9,7], \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrContainsAllNegJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all([1, 9,7], \"/val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrIn() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrInJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"/needle\", \"/haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrInWithNoExceptionOnNullArr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator(new InjectValueErrorHandlingStrategy()).evaluate(operator, node));
    }

    @Test
    void testFuncArrInWithNoExceptionOnNullArrJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"/needle\", \"/haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator(new InjectValueErrorHandlingStrategy()).evaluate(operator, node));
    }

    @Test
    void testFuncArrInWithNullArr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrInWithNullArrJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"/needle\", \"/haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrNotIn() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("arr.not_in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrNotInJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("arr.not_in(\"/needle\", \"/haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrIsEmpty() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }");

        HopeParser parser = new HopeParser(new StringReader("arr.is_empty(\"$.empty\") == true  && false == arr.is_empty(\"$.nonempty\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrIsEmptyJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }");

        HopeParser parser = new HopeParser(new StringReader("arr.is_empty(\"/empty\") == true  && false == arr.is_empty(\"/nonempty\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrLen() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"array\" : [1,2,3, 4,8,16]}");

        HopeParser parser = new HopeParser(new StringReader("arr.len(\"$.array\") == 6"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncArrLenJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"array\" : [1,2,3, 4,8,16]}");

        HopeParser parser = new HopeParser(new StringReader("arr.len(\"/array\") == 6"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncPerf() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");

        final HopeLangEngine hopeLangParser = HopeLangEngine.builder()
                .build();

        final String payload = "\"$.value\" > 11" +
                " && \"$.value\" <30" +
                " && \"$.value\" > 19 && \"$.value\" < 21" +
                " && \"$.value\" >=20 && \"$.value\" < 21" +
                " && \"$.value\" > 11 && \"$.value\" <= 20";
        System.out.println(payload);
        val rule = hopeLangParser.parse(
                payload);

        IntStream.range(0, 10)
                .forEach(times -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    IntStream.range(1, 1_000_000)
                            .forEach(i -> hopeLangParser.evaluate(rule, node));
                    log.info("Time taken: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                });
    }

    @Test
    void testFuncPerfJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"value\": 20, \"string\" : \"Hello\" }");

        final HopeLangEngine hopeLangParser = HopeLangEngine.builder()
                .build();

        final String payload = "\"/value\" > 11" +
                " && \"/value\" <30" +
                " && \"/value\" > 19 && \"/value\" < 21" +
                " && \"/value\" >=20 && \"/value\" < 21" +
                " && \"/value\" > 11 && \"/value\" <= 20";
        System.out.println(payload);
        val rule = hopeLangParser.parse(
                payload);

        IntStream.range(0, 10)
                .forEach(times -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    IntStream.range(1, 1_000_000)
                            .forEach(i -> hopeLangParser.evaluate(rule, node));
                    log.info("Time taken: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                });
    }


    @Test
    void testFuncStrLenFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLenFailNoNodeJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"/val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }


    @Test
    void testFuncStrLenFailBadType() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : 29 }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeTypeMismatchError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncStrLenFailBadTypeJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : 29 }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"/val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeTypeMismatchError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncIntFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"$.val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncIntFailNoNodeJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"/val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncIntFailBadType() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"Blah\" }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"$.val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeTypeMismatchError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncIntFailBadTypeJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"Blah\" }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"/val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeTypeMismatchError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncBoolFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("\"$.val\" == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

    @Test
    void testFuncBoolFailNoNodeJPtr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("\"/val\" == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> new Evaluator().evaluate(operator, node));
    }

}
