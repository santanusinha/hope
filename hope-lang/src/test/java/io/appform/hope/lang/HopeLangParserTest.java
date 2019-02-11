package io.appform.hope.lang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.Value;
import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.JsonPathValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.values.StringValue;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

/**
 *
 */
public class HopeLangParserTest {

    final JsonNode node = NullNode.getInstance();

    @Test
    public void testInt() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1"));
        final Value operand = parser.NumericRepr();
        Assert.assertTrue(operand instanceof NumericValue);
        Assert.assertEquals(1.0, NumericValue.class.cast(operand).getValue());
    }

    @Test
    public void testDouble() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("1.5"));
        final Value operand = parser.NumericRepr();
        Assert.assertTrue(operand instanceof NumericValue);
        Assert.assertEquals(1.5, NumericValue.class.cast(operand).getValue());
    }

    @Test
    public void testBoolean() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true"));
        final Value operand = parser.BooleanRepr();
        Assert.assertTrue(operand instanceof BooleanValue);
        Assert.assertTrue(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    public void testBooleanFalse() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false"));
        final Value operand = parser.BooleanRepr();
        Assert.assertTrue(operand instanceof BooleanValue);
        Assert.assertFalse(BooleanValue.class.cast(operand).getValue());
    }

    @Test
    public void testString() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"abc\""));
        final Value operand = parser.StringRepr();
        Assert.assertTrue(operand instanceof StringValue);
        Assert.assertEquals("abc", StringValue.class.cast(operand).getValue());
    }

    @Test
    public void testPath() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("\"$.abc\""));
        final Value operand = parser.JsonPathRepr();
        Assert.assertTrue(operand instanceof JsonPathValue);
        Assert.assertEquals("$.abc", JsonPathValue.class.cast(operand).getPath());
    }

    @Test
    public void testEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 23"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 == 29"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testNotEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 29"));
        final Evaluatable operator = parser.parse();
        //Assert.assertEquals("$.abc", JsonPathValue.class.cast(operand).getPath());
        System.out.println(operator);
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testNotEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("23 != 23"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & true"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true & false"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | true"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrSuccess2() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("true | false"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("false | false"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 > 2"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 > 3"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testGreaterEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 >= 3"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 < 3"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 < 2"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLesserEqualsSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("2 <= 3"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLessEqualsFail() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 <= 2"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testLessEqualsFailParenthesis() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 <= 2)"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 && 2 < 5"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testAndCombinerFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) && (2 < 5)"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrCombinerSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("3 >= 2 || 2 < 5"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testOrCombinerSuccessRhs() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(3 == 2) || (2 < 5)"));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexSuccess() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("((3 >= 2) && (2 < 5)) || 2 == 2 "));
        final Evaluatable operator = parser.parse();
        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexFailure() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 >= 2) && (2 == 5)) && 2 == 2)"));
        final Evaluatable operator = parser.parse();
        System.out.println(operator);
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testCombinerComplexSuccessBothSides() throws Exception {
        HopeParser parser = new HopeParser(new StringReader("(((3 <= 2) || (2 == 5)) && ((2 == 2) ||  (2 == 1)))"));
        final Evaluatable operator = parser.parse();
        Assert.assertFalse(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"name\" : \"santanu\" }");

        HopeParser parser = new HopeParser(new StringReader("\"$.name\" == \"santanu\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumeric() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" == 93"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("94 > \"$.count\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" > 94"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericGreaterBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 97, \"rhs\" : 94 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" > \"$.rhs\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserRhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("92 < \"$.count\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserLhs() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 91 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.count\" < 94"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathNumericLesserBothSide() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"lhs\" : 94, \"rhs\" : 97 }");

        HopeParser parser = new HopeParser(new StringReader("\"$.lhs\" < \"$.rhs\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimple() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^false"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimplePath() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("^\"$.boolValue\""));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotSimplePathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\")"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotCombinerPathParenth() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num\" : 43 }");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && \"$.num\" > 40)"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void  testNotCombinerPathParenthComplex() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"boolValue\" : false, \"num1\" : 43, \"num2\" : 49}");

        HopeParser parser = new HopeParser(new StringReader("(^\"$.boolValue\" && (\"$.num1\" > 45 || \"$.num2\" < 50))"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAbs() throws Exception {
        //FunctionRegistry.discover();
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"count\" : 93 }");

        HopeParser parser = new HopeParser(new StringReader("93 <= math.abs(\"$.count\")"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAdd() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= math.add(\"$.a\", \"$.b\", 7)"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncSysEpoch() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("7 <= sys.epoch()"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testJsonPathFuncMathAddLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("math.add(\"$.a\", \"$.b\", 7) == 12"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }

    @Test
    public void testFuncSysEpochLHS() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("{ \"a\" : 2, \"b\" : 3 }");

        HopeParser parser = new HopeParser(new StringReader("sys.epoch() >= 7"));
        final Evaluatable operator = parser.parse();

        Assert.assertTrue(new Evaluator().evaluate(operator, node));
    }
}