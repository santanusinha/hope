package io.appform.hope.core.operators;

import io.appform.hope.core.values.BooleanValue;
import io.appform.hope.core.values.JsonPathValue;
import io.appform.hope.core.values.NumericValue;
import io.appform.hope.core.values.StringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatorEqualityTest {

    @Test
    void OrEquality() {
        Or o1 = new Or(new BooleanValue(true), new BooleanValue(true));
        Or o2 = new Or(new BooleanValue(true), new BooleanValue(true));
        assertEquals(o1, o2);
    }

    @Test
    void AndEquality() {
        BooleanValue b11 = new BooleanValue(false);
        BooleanValue b12 = new BooleanValue(true);
        BooleanValue b21 = new BooleanValue(false);
        BooleanValue b22 = new BooleanValue(true);
        And a1 = new And(b11, b12);
        And a2 = new And(b21, b22);
        assertEquals(a2, a1);
        assertEquals(a2.hashCode(), a1.hashCode());
    }

    @Test
    void Greater_Equality() {
        NumericValue n11 = new NumericValue(2);
        NumericValue n12 = new NumericValue(6);
        NumericValue n21 = new NumericValue(2);
        NumericValue n22 = new NumericValue(6);
        Greater g1 = new Greater(n11, n12);
        Greater g2 = new Greater(n21, n22);
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void GreaterEquals_Equality() {
        NumericValue n11 = new NumericValue(10);
        NumericValue n12 = new NumericValue(8);
        NumericValue n21 = new NumericValue(10);
        NumericValue n22 = new NumericValue(8);
        GreaterEquals g1 = new GreaterEquals(n11, n12);
        GreaterEquals g2 = new GreaterEquals(n21, n22);
        assertEquals(g1, g2);
        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void Lesser_Equality() {
        NumericValue n11 = new NumericValue(234);
        NumericValue n12 = new NumericValue(45);
        NumericValue n21 = new NumericValue(234);
        NumericValue n22 = new NumericValue(45);
        Lesser l1 = new Lesser(n11, n12);
        Lesser l2 = new Lesser(n21, n22);
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    @Test
    void LesserEquals_Equality() {
        NumericValue n11 = new NumericValue(10);
        NumericValue n12 = new NumericValue(789);
        NumericValue n21 = new NumericValue(10);
        NumericValue n22 = new NumericValue(789);
        LesserEquals l1 = new LesserEquals(n11, n12);
        LesserEquals l2 = new LesserEquals(n21, n22);
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }
    @Test
    void EqualsEquality() {
        JsonPathValue jpv1 = new JsonPathValue("providerId");
        JsonPathValue jpv2 = new JsonPathValue("providerId");
        StringValue sv1 = new StringValue("UTIB");
        StringValue sv2 = new StringValue("UTIB");
        Equals equals1 = new Equals(jpv1, sv1);
        Equals equals2 = new Equals(jpv2, sv2);
        assertEquals(equals1, equals2);
        assertEquals(equals1.hashCode(), equals2.hashCode());
    }

}