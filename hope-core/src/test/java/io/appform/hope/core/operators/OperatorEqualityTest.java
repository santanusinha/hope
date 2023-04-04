package io.appform.hope.core.operators;

import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.JsonPathValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.values.StringValue;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorEqualityTest {

    @Test
    void OrEquality() {
        val o1 = new Or(new BooleanValue(true), new BooleanValue(true));
        val o2 = new Or(new BooleanValue(true), new BooleanValue(true));
        assertEquals(o1, o2);
    }

    @Test
    void AndEquality() {
        val a1 = new And(new BooleanValue(false), new BooleanValue(true));
        val a2 = new And(new BooleanValue(false), new BooleanValue(true));
        assertEquals(a2, a1);
        assertEquals(a2.hashCode(), a1.hashCode());
    }

    @Test
    void Greater_Equality() {
        val g1 = new Greater(new NumericValue(2), new NumericValue(6));
        val g2 = new Greater(new NumericValue(2), new NumericValue(6));
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void GreaterEquals_Equality() {
        val g1 = new GreaterEquals(new NumericValue(10), new NumericValue(8));
        val g2 = new GreaterEquals(new NumericValue(10), new NumericValue(8));
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void Lesser_Equality() {
        val l1 = new Lesser(new NumericValue(234), new NumericValue(45));
        val l2 = new Lesser(new NumericValue(234), new NumericValue(45));
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    @Test
    void LesserEquals_Equality() {
        val l1 = new LesserEquals(new NumericValue(10), new NumericValue(789));
        val l2 = new LesserEquals(new NumericValue(10), new NumericValue(789));
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    @Test
    void EqualsEquality() {
        val equals1 = new Equals(new JsonPathValue("providerId"), new StringValue("UTIB"));
        val equals2 = new Equals(new JsonPathValue("providerId"), new StringValue("UTIB"));
        assertEquals(equals1, equals2);
        assertEquals(equals1.hashCode(), equals2.hashCode());
    }
}