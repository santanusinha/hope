package io.appform.hope.core.visitors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import io.appform.hope.core.operators.Equals;
import io.appform.hope.core.values.ObjectValue;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class EvaluatorTest {

    final JsonNode node = NullNode.getInstance();

    @Test
    public void basicTest() {
        final Evaluator evaluator = new Evaluator();
        Assert.assertTrue(
                evaluator.evaluate(
                        Equals.builder()
                                .lhs(ObjectValue.builder()
                                             .value("a")
                                             .build())
                                .rhs(ObjectValue.builder()
                                             .value("a")
                                             .build())
                                .build()
                        , node))
        ;
        Assert.assertFalse(
                evaluator.evaluate(
                        Equals.builder()
                                .lhs(ObjectValue.builder()
                                             .value("a")
                                             .build())
                                .rhs(ObjectValue.builder()
                                             .value("b")
                                             .build())
                                .build()
                        , node));
    }

}