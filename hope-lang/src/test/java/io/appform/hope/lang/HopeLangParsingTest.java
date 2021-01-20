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
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.JsonPathValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.values.StringValue;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Tests hope lang parsing
 */
@Slf4j
public class HopeLangParsingTest {
    final JsonNode node = NullNode.getInstance();
    final FunctionRegistry functionRegistry;

    public HopeLangParsingTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @Test
    public void testInt() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1"));
        final Value operand = parser.NumericRepr();
        Assert.assertEquals(1.0, NumericValue.class.cast(operand).getValue());
    }

    @Test
    public void testDouble() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1.5"));
        final Value operand = parser.NumericRepr();
        Assert.assertEquals(1.5, NumericValue.class.cast(operand).getValue());
    }

    @Test
    public void testBoolean() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true"));
        final Value operand = parser.BooleanRepr();
        Assert.assertTrue(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    public void testBooleanFalse() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false"));
        final Value operand = parser.BooleanRepr();
        Assert.assertFalse(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    public void testString() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"abc\""));
        final Value operand = parser.StringRepr();
        Assert.assertEquals("abc", StringValue.class.cast(operand).getValue());
    }

    @Test
    public void testPath() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"$.abc\""));
        final Value operand = parser.JsonPathRepr();
        Assert.assertEquals("$.abc", JsonPathValue.class.cast(operand).getPath());
    }

    @Test
    public void testEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 23"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 29"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testNotEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 29"));
        final Evaluatable operator = parser.parse(functionRegistry);
        //Assert.assertEquals("$.abc", JsonPathValue.class.cast(operand).getPath());
        System.out.println(operator);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testNotEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 23"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & true"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | true"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrSuccess2() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false | false"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 > 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 > 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 >= 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 < 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 < 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 <= 3"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLessEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 <= 2"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLessEqualsFailParenthesis() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 <= 2)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 && 2 < 5"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndCombinerFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) && (2 < 5)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 || 2 < 5"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrCombinerSuccessRhs() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) || (2 < 5)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("((3 >= 2) && (2 < 5)) || 2 == 2 "));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 >= 2) && (2 == 5)) && 2 == 2)"));
        final Evaluatable operator = parser.parse(functionRegistry);
        System.out.println(operator);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexSuccessBothSides() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 <= 2) || (2 == 5)) && ((2 == 2) ||  (2 == 1)))"));
        final Evaluatable operator = parser.parse(functionRegistry);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"name\" : \"santanu\" }");

        HopeParser parser = new HopeParser(new StringReader("\"$.name\" == \"santanu\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumeric() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" == 93"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("94 > \"$.count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" > 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 97, \"rhs\" : 94 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" > \"$.rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("92 < \"$.count\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 91 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" < 94"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 94, \"rhs\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" < \"$.rhs\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimple() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^false"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimplePath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^\"$.boolValue\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimplePathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotCombinerPathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num\" : 43 }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && \"$.num\" > 40)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotCombinerPathParenthComplex() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && (\"$.num1\" > 45 || \"$.num2\" < 50))"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAbs() throws Exception {
        //FunctionRegistry.discover();
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("93 <= math.abs(\"$.count\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAdd() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= math.add(\"$.a\", \"$.b\", 7)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncSysEpoch() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= sys.epoch()"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncUtilsHashJ() throws Exception {
        System.out.println("Hash" + new Integer(-23).hashCode());
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.abs(utils.hash_j(-23)) == math.abs(-1070137344)"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncUtilsHashM128() throws Exception {
        System.out.println("Hash" + new Integer(-23).hashCode());
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.abs(utils.hash_m128(-23)) == 2940310638642342768"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAddLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(\"$.a\", \"$.b\", 7) == 12"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAddNested() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(math.add(\"$.a\", \"$.b\", 7), \"$.c\", 4) == 25"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathProdLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.prod(\"$.a\", \"$.b\", 7) == 42"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathNestedLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(math.sub(math.add(\"$.a\", \"$.b\", 7), 13)) == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncSysEpochLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("sys.epoch() >= 7"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrLower() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"ABC\" }");

        HopeParser parser = new HopeParser(new StringReader("str.lower(\"$.val\") == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrUpper() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"$.val\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrUpperSnakeCase() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val_txt\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.upper(\"$.val_txt\") == \"ABC\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrLen() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrMatch() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abc\" }");

        HopeParser parser = new HopeParser(new StringReader("str.match(\"^a.*\", \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrSubStr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"$.val\", 0, 3) == \"abc\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncStrSubStrNoEndOverload() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("str.substr(\"$.val\", 3) == \"def\""));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncPathExists() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"abcdef\" }");

        HopeParser parser = new HopeParser(new StringReader("path.exists(\"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrContainsAny() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any(\"$.val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrContainsAnyNeg() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_any([9,7], \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrContainsAll() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,3, 4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all(\"$.val\", [2,3]) == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrContainsAllNeg() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : [1,2,4,8,16] }");

        HopeParser parser = new HopeParser(new StringReader("arr.contains_all([1, 9,7], \"$.val\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrIn() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrInWithNoExceptionOnNullArr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertFalse(new Evaluator(new InjectValueErrorHandlingStrategy()).evaluate(operator, node));
    }

    @Test(expected = HopeMissingValueError.class)
    public void testFuncArrInWithNullArr() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{\"needle\" : 2 }");

        HopeParser parser = new HopeParser(new StringReader("arr.in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrNotIn() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }");

        HopeParser parser = new HopeParser(new StringReader("arr.not_in(\"$.needle\", \"$.haystack\") == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrIsEmpty() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }");

        HopeParser parser = new HopeParser(new StringReader("arr.is_empty(\"$.empty\") == true  && false == arr.is_empty(\"$.nonempty\")"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncArrLen() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"array\" : [1,2,3, 4,8,16]}");

        HopeParser parser = new HopeParser(new StringReader("arr.len(\"$.array\") == 6"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncPerf() throws Exception {
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
                .forEach( times -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    IntStream.range(1, 1_000_000)
                            .forEach(i -> hopeLangParser.evaluate(rule, node));
                    log.info("Time taken: {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                });

    }


    @Test(expected = HopeMissingValueError.class)
    public void testFuncStrLenFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }


    @Test(expected = HopeTypeMismatchError.class)
    public void testFuncStrLenFailBadType() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : 29 }");

        HopeParser parser = new HopeParser(new StringReader("str.len(\"$.val\") == 3"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test(expected = HopeMissingValueError.class)
    public void testFuncIntFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"$.val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test(expected = HopeTypeMismatchError.class)
    public void testFuncIntFailBadType() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"val\" : \"Blah\" }");

        HopeParser parser = new HopeParser(new StringReader("math.negate(\"$.val\") == 1"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test(expected = HopeMissingValueError.class)
    public void testFuncBoolFailNoNode() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{  }");

        HopeParser parser = new HopeParser(new StringReader("\"$.val\" == true"));
        final Evaluatable operator = parser.parse(functionRegistry);

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

}
