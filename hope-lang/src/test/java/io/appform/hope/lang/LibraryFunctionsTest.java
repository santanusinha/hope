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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.exceptions.errorstrategy.InjectValueErrorHandlingStrategy;
import io.appform.hope.core.exceptions.impl.HopeMissingValueError;
import io.appform.hope.core.exceptions.impl.HopeTypeMismatchError;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.core.visitors.Evaluator;
import io.appform.hope.lang.parser.HopeParser;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests library functions
 */
class LibraryFunctionsTest {

    final ObjectMapper mapper = new ObjectMapper();

    final FunctionRegistry functionRegistry;

    LibraryFunctionsTest() {
        this.functionRegistry = new FunctionRegistry();
        functionRegistry.discover(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("rules")
    @SneakyThrows
    void testRules(final String json, final String rule, boolean expectation) {
        val node = mapper.readTree(json);
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);

        assertEquals(expectation, new Evaluator(new InjectValueErrorHandlingStrategy()).evaluate(operator, node));
    }

    @ParameterizedTest
    @MethodSource("rulesNonExistentItems")
    @SneakyThrows
    void testMissingValueDefault(final String json, final String rule) {
        val node = mapper.readTree(json);
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);

        assertFalse(new Evaluator(new InjectValueErrorHandlingStrategy()).evaluate(operator, node));
    }

    @ParameterizedTest
    @MethodSource("rulesNonExistentItems")
    @SneakyThrows
    void testMissingValueError(final String json, final String rule) {
        val node = mapper.readTree(json);
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);

        assertThrows(HopeMissingValueError.class, () -> evaluate(operator, node));
    }

    @ParameterizedTest
    @MethodSource("rulesWrongTypes")
    @SneakyThrows
    void testWrongTypeError(final String json, final String rule) {
        val node = mapper.readTree(json);
        val parser = new HopeParser(new StringReader(rule));
        val operator = parser.parse(functionRegistry);

        assertThrows(HopeTypeMismatchError.class, () -> evaluate(operator, node));
    }

    private static Stream<Arguments> rulesWrongTypes() {
        return Stream.of(
                Arguments.of("{ \"val\" : 29 }", "str.len(\"$.val\") == 3"),
                Arguments.of("{ \"val\" : 29 }", "str.len('$.val') == 3"),
                Arguments.of("{ \"val\" : 29 }", "str.len(\"/val\") == 3"),
                Arguments.of("{ \"val\" : 29 }", "str.len('/val') == 3"),
                Arguments.of("{ \"val\" : \"Blah\" }", "math.negate(\"$.val\") == 1"),
                Arguments.of("{ \"val\" : \"Blah\" }", "math.negate('$.val') == 1"),
                Arguments.of("{ \"val\" : \"Blah\" }", "math.negate(\"/val\") == 1"),
                Arguments.of("{ \"val\" : \"Blah\" }", "math.negate('/val') == 1")
                        );
    }

    private static Stream<Arguments> rulesNonExistentItems() {
        return Stream.of(
                Arguments.of("{\"needle\" : 2 }", "arr.in(\"$.needle\", \"$.haystack\") == true"),
                Arguments.of("{\"needle\" : 2 }", "arr.in('$.needle', '$.haystack') == true"),
                Arguments.of("{\"needle\" : 2 }", "arr.in('/needle', '/haystack') == true"),
                Arguments.of("{\"needle\" : 2 }", "arr.in('/needle', '/haystack') == true"),
                Arguments.of("{}", "str.len(\"$.val\") == 3"),
                Arguments.of("{}", "str.len('$.val') == 3"),
                Arguments.of("{}", "str.len(\"/val\") == 3"),
                Arguments.of("{}", "str.len('$.val') == 3"),
                Arguments.of("{}", "math.negate(\"$.val\") == 1"),
                Arguments.of("{}", "math.negate('$.val') == 1"),
                Arguments.of("{}", "math.negate(\"/val\") == 1"),
                Arguments.of("{}", "math.negate('/val') == 1"),
                Arguments.of("{}", "\"$.val\" == true"),
                Arguments.of("{}", "'$.val' == true"),
                Arguments.of("{}", "\"/val\" == true"),
                Arguments.of("{}", "'/val' == true")

                );
    }

    private static Stream<Arguments> rules() {
        final var dateTime = LocalDateTime.now();
        final long epochMilli = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        final long weekOfMonth = dateTime.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
        final long weekOfYear = dateTime.get(WeekFields.of(Locale.getDefault()).weekOfYear());

        return Stream.of(
                Arguments.of("{ \"count\" : 93 }", "93 <= math.abs(\"$.count\")", true),
                Arguments.of("{ \"count\" : 93 }", "93 <= math.abs('$.count')", true),
                Arguments.of("{ \"count\" : 93 }", "93 <= math.abs(\"/count\")", true),
                Arguments.of("{ \"count\" : 93 }", "93 <= math.abs('/count')", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "7 <= math.add(\"$.a\", \"$.b\", 7)", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "7 <= math.add('$.a', '$.b', 7)", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "7 <= math.add(\"/a\", \"/b\", 7)", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "7 <= math.add('/a', '/b', 7)", true),
                Arguments.of("{}", "7 <= sys.epoch()", true),
                Arguments.of("{}", "math.abs(utils.hash_j(-23)) == math.abs(-1070137344)", true),
                Arguments.of("{}", "math.abs(utils.hash_m128(-23)) == 2940310638642342768", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.add(\"$.a\", \"$.b\", 7) == 12", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.add('$.a', '$.b', 7) == 12", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.add(\"/a\", \"/b\", 7) == 12", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.add('/a', '/b', 7) == 12", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.add('/a', '/b', 7) == 12", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }", "math.add(math.add(\"$.a\", \"$.b\", 7), \"$.c\", 4) == 25", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }", "math.add(math.add('$.a', '$.b', 7), '$.c', 4) == 25", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }", "math.add(math.add(\"/a\", \"/b\", 7), \"/c\", 4) == 25", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 9 }", "math.add(math.add('/a', '/b', 7), '/c', 4) == 25", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.prod(\"$.a\", \"$.b\", 7) == 42", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.prod('$.a', '$.b', 7) == 42", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.prod(\"/a\", \"/b\", 7) == 42", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3 }", "math.prod('/a', '/b', 7) == 42", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }", "math.negate(math.sub(math.add(\"$.a\", \"$.b\", 7), 13)) == 1", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }", "math.negate(math.sub(math.add('$.a', '$.b', 7), 13)) == 1", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }", "math.negate(math.sub(math.add(\"/a\", \"/b\", 7), 13)) == 1", true),
                Arguments.of("{ \"a\" : 2, \"b\" : 3, \"c\" : 5 }", "math.negate(math.sub(math.add('/a', '/b', 7), 13)) == 1", true),
                Arguments.of("{}", "sys.epoch() >= 7", true),
                Arguments.of("{ \"val\" : \"ABC\" }", "str.lower(\"$.val\") == \"abc\"", true),
                Arguments.of("{ \"val\" : \"ABC\" }", "str.lower('$.val') == 'abc'", true),
                Arguments.of("{ \"val\" : \"ABC\" }", "str.lower(\"/val\") == \"abc\"", true),
                Arguments.of("{ \"val\" : \"ABC\" }", "str.lower('/val') == 'abc'", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.upper(\"$.val\") == \"ABC\"", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.upper('$.val') == 'ABC'", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.upper(\"/val\") == \"ABC\"", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.upper('/val') == 'ABC'", true),
                Arguments.of("{ \"val_txt\" : \"abc\" }", "str.upper(\"$.val_txt\") == \"ABC\"", true),
                Arguments.of("{ \"val_txt\" : \"abc\" }", "str.upper('$.val_txt') == 'ABC'", true),
                Arguments.of("{ \"val_txt\" : \"abc\" }", "str.upper(\"/val_txt\") == \"ABC\"", true),
                Arguments.of("{ \"val_txt\" : \"abc\" }", "str.upper('/val_txt') == 'ABC'", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.len(\"$.val\") == 3", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.len('$.val') == 3", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.len(\"/val\") == 3", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.len('/val') == 3", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.match(\"^a.*\", \"$.val\") == true", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.match('^a.*', '$.val') == true", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.match(\"^a.*\", \"/val\") == true", true),
                Arguments.of("{ \"val\" : \"abc\" }", "str.match('^a.*', '/val') == true", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr(\"$.val\", 0, 3) == \"abc\"", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr('$.val', 0, 3) == 'abc'", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr(\"/val\", 0, 3) == \"abc\"", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr('/val', 0, 3) == 'abc'", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr(\"$.val\", 3) == \"def\"", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr('$.val', 3) == 'def'", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr(\"/val\", 3) == \"def\"", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "str.substr('/val', 3) == 'def'", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "path.exists(\"$.val\") == true", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "path.exists('$.val') == true", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "pointer.exists(\"/val\") == true", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "pointer.exists('/val') == true", true),
                Arguments.of("{ \"val\" : \"abcdef\" }", "pointer.exists('/xxx') == true", false),
                Arguments.of("{ \"val\" : \"abcdef\" }", "path.exists('$.xxx') == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any(\"$.val\", [2,3]) == true", true),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any('$.val', [2,3]) == true", true),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any(\"/val\", [2,3]) == true", true),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any('/val', [2,3]) == true", true),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any([9,7], \"$.val\") == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any([9,7], '$.val') == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any([9,7], \"/val\") == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_any([9,7], '/val') == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all(\"/val\", [2,3]) == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all('$.val', [2,3]) == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all([1, 9,7], \"$.val\") == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all([1, 9,7], '$.val') == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all([1, 9,7], \"/val\") == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all([1, 9,7], '/val') == true", false),
                Arguments.of("{ \"val\" : [1,2,4,8,16] }", "arr.contains_all('/val', [2,3]) == true", false),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('$.needle', '$.haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"/needle\", \"/haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('/needle', '/haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('$.needle', '$.haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"/needle\", \"/haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('/needle', '/haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('$.needle', '$.haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in(\"/needle\", \"/haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 2 }", "arr.in('/needle', '/haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }", "arr.not_in(\"$.needle\", \"$.haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }", "arr.not_in('$.needle', '$.haystack') == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }", "arr.not_in(\"/needle\", \"/haystack\") == true", true),
                Arguments.of("{ \"haystack\" : [1,2,3, 4,8,16], \"needle\" : 9 }", "arr.not_in('/needle', '/haystack') == true", true),
                Arguments.of("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }", "arr.is_empty(\"$.empty\") == true  && false == arr.is_empty(\"$.nonempty\")", true),
                Arguments.of("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }", "arr.is_empty('$.empty') == true  && false == arr.is_empty('$.nonempty')", true),
                Arguments.of("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }", "arr.is_empty(\"/empty\") == true  && false == arr.is_empty(\"/nonempty\")", true),
                Arguments.of("{ \"nonempty\" : [1,2,3, 4,8,16], \"empty\" : [] }","arr.is_empty('/empty') == true  && false == arr.is_empty('/nonempty')", true),
                Arguments.of("{ \"array\" : [1,2,3, 4,8,16] }","arr.len(\"$.array\") == 6", true),
                Arguments.of("{ \"array\" : [1,2,3, 4,8,16] }","arr.len('$.array') == 6", true),
                Arguments.of("{ \"array\" : [1,2,3, 4,8,16] }","arr.len(\"/array\") == 6", true),
                Arguments.of("{ \"array\" : [1,2,3, 4,8,16] }","arr.len('/array') == 6", true),

                /* below set tests all date functions.
                * Note: We generate a time at the start of the test and compare the generated values from the builtin function with this time.
                * But in the off chance that the test is running at the 999th millisecond of the second, the diff can be more than 1 (00-59 = -59)
                * And the same analogy applies for minute/hour/week etc as all the values are like circular buffers [0,1.2...59..0,1..]
                * The tests below handle this (so unfortunately, it is a little complicated) */

                Arguments.of("{}", "(math.abs(math.sub(date.now(), %d)) <= 3000 && math.abs(math.sub(date.now(), %d)) >= 0) || (math.abs(math.sub(date.now(), %d)) >= 58000 && math.abs(math.sub(date.now(), %d)) <= 60000)".formatted(epochMilli, epochMilli, epochMilli, epochMilli), true),
                Arguments.of("{}", "(math.abs(math.sub(date.second_of_minute(), %d)) <= 10 && math.abs(math.sub(date.second_of_minute(), %d)) >= 0) || (math.abs(math.sub(date.second_of_minute(), %d)) <= 60 && math.abs(math.sub(date.second_of_minute(), %d)) >= 58)".formatted(dateTime.getSecond(), dateTime.getSecond(), dateTime.getSecond(), dateTime.getSecond()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.minute_of_hour(), %d)) <= 1 && math.abs(math.sub(date.minute_of_hour(), %d)) >= 0) || (math.abs(math.sub(date.minute_of_hour(), %d)) <= 60 && math.abs(math.sub(date.minute_of_hour(), %d)) >= 58)".formatted(dateTime.getMinute(), dateTime.getMinute(), dateTime.getMinute(), dateTime.getMinute()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.hour_of_day(), %d)) <= 1 && math.abs(math.sub(date.hour_of_day(), %d)) >= 0) || (math.abs(math.sub(date.hour_of_day(), %d)) <= 24 && math.abs(math.sub(date.hour_of_day(), %d)) >= 23)".formatted(dateTime.getHour(), dateTime.getHour(), dateTime.getHour(), dateTime.getHour()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.day_of_week(), %d)) <= 1 && math.abs(math.sub(date.day_of_week(), %d)) >= 0) || (math.abs(math.sub(date.day_of_week(), %d)) <= 7 && math.abs(math.sub(date.day_of_week(), %d)) >= 6)".formatted(dateTime.getDayOfWeek().getValue(), dateTime.getDayOfWeek().getValue(), dateTime.getDayOfWeek().getValue(), dateTime.getDayOfWeek().getValue()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.day_of_month(), %d)) <= 1 && math.abs(math.sub(date.day_of_month(), %d)) >= 0) || (math.abs(math.sub(date.day_of_month(), %d)) <= 31 && math.abs(math.sub(date.day_of_month(), %d)) >= 27)".formatted(dateTime.getDayOfMonth(), dateTime.getDayOfMonth(), dateTime.getDayOfMonth(), dateTime.getDayOfMonth()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.day_of_year(), %d)) <= 1 && math.abs(math.sub(date.day_of_year(), %d)) >= 0) || (math.abs(math.sub(date.day_of_year(), %d)) <= 366 && math.abs(math.sub(date.day_of_year(), %d)) >= 364)".formatted(dateTime.getDayOfYear(), dateTime.getDayOfYear(), dateTime.getDayOfYear(), dateTime.getDayOfYear()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.week_of_month(), %d)) <= 1 && math.abs(math.sub(date.week_of_month(), %d)) >= 0) || (math.abs(math.sub(date.week_of_month(), %d)) <= 6 && math.abs(math.sub(date.week_of_month(), %d)) >= 3)".formatted(weekOfMonth, weekOfMonth, weekOfMonth, weekOfMonth), true),
                Arguments.of("{}", "(math.abs(math.sub(date.week_of_year(), %d)) <= 1 && math.abs(math.sub(date.week_of_year(), %d)) >= 0) || (math.abs(math.sub(date.week_of_year(), %d)) <= 54 && math.abs(math.sub(date.week_of_year(), %d)) >= 51)".formatted(weekOfYear, weekOfYear, weekOfYear, weekOfYear), true),
                Arguments.of("{}", "(math.abs(math.sub(date.month_of_year(), %d)) <= 1 && math.abs(math.sub(date.month_of_year(), %d)) >= 0) || (math.abs(math.sub(date.month_of_year(), %d)) <= 12 && math.abs(math.sub(date.month_of_year(), %d)) >= 11)".formatted(dateTime.getMonth().getValue(), dateTime.getMonth().getValue(), dateTime.getMonth().getValue(), dateTime.getMonth().getValue()), true),
                Arguments.of("{}", "(math.abs(math.sub(date.year(), %d)) <= 1 && math.abs(math.sub(date.year(), %d)) >= 0)".formatted(dateTime.getYear(), dateTime.getYear()), true)

                 );
    }

    private static void evaluate(Evaluatable operator, JsonNode node) {
        new Evaluator().evaluate(operator, node);
    }
}

