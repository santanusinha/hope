package io.appform.hope.lang;

import io.appform.hope.core.Evaluatable;
import io.appform.hope.core.functions.FunctionRegistry;
import io.appform.hope.lang.parser.HopeParser;

import java.io.StringReader;

/**
 *
 */
public class HopeLangParser {
    private final FunctionRegistry functionRegistry;


    public HopeLangParser(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }

    public Evaluatable parse(final String payload) throws Exception {
        return new HopeParser(new StringReader(payload)).parse();
    }
}
