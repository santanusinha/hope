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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
@Slf4j
class HopeLangEngineTest {

    @Test
    void testFuncIntFailNoExceptNoNode() throws Exception {
        testRuleFalse("""
                              "$.x" == true""");
    }

    @Test
    void testFuncIntFailNoExceptNoNodeSQ() throws Exception {
        testRuleFalse("""
                              '$.x' == true""");
    }

    @Test
    void testFuncIntFailNoExceptNoNodeJPtr() throws Exception {
        testRuleFalse("""
                              "/x" == true""");
    }

    @Test
    void testEvaluateFirst() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.createObjectNode().put("x", "A");
        final HopeLangEngine hopeLangParser = HopeLangEngine.builder()
                .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
                .build();
        List<Evaluatable> evaluatables = Lists.newArrayList(
                hopeLangParser.parse("\"$.x\" == \"E\""),
                hopeLangParser.parse("\"$.x\" == \"A\""),
                hopeLangParser.parse("\"$.y\" == \"U\"")
                                                           );
        Integer matchedRuleIndex = hopeLangParser.evaluateFirst(evaluatables, node).orElse(-1);
        Assertions.assertEquals(1, matchedRuleIndex);
    }

    @Test
    void testBlah() throws Exception {

        final HopeLangEngine hope
                = HopeLangEngine.builder()
                .registerFunction(Blah.class) //Register class by class
                .build();

        JsonNode node = new ObjectMapper().readTree("{}");
        assertTrue(hope.evaluate("ss.blah() == \"blah\"", node));
    }

    private static void testRuleFalse(String hopeLangExpression) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = mapper.readTree("");
        final HopeLangEngine hopeLangParser = HopeLangEngine.builder()
                .errorHandlingStrategy(new InjectValueErrorHandlingStrategy())
                .build();

        final Evaluatable operator = hopeLangParser.parse(hopeLangExpression);

        //NOTE::THIS IS HOW THE BEHAVIOUR IS FOR EQUALS/NOT_EQUALS:
        //BASICALLY THE NODE WILL EVALUATE TO NULL AND WILL MISMATCH EVERYTHING
        assertFalse(hopeLangParser.evaluate(operator, node));
    }
}