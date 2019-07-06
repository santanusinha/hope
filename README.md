# Hope
Hope is a high level language for predicate evaluation on JSON written in java. It uses jackson for json handling and json-path for value extraction.

#### Sample Hope expressions/rules
```
 "$.val" + 2 > 3
 "$.a" + "$.b" <= math.abs("$.c")
 ```
 
#### Features
- Simple syntax
- Bunch of rich standard library functions built in
- Performant
- Easy to add custom functions
# Getting started

### Maven dependency
Use the following dependency in your code.
```
    <dependency>
        <groupId>io.appform.hope</groupId>
        <artifactId>hope-lang</artifactId>
        <version>1.0.5</version>
    </dependency>
```

### Basic usage
The following steps can be used to parse and evaluate a predicate expression or rule.
#### Create HopeLangEngine
The main class you need to know is `HopeLangEngine`. To use this class use the provided builder.
```
    final HopeLangEngine hope = HopeLangEngine.builder()
                                .build();
```
> **Note:** Creation of this class is a time consuming affair. Create and resue this. This class is thread-safe.
#### Evaluate an expression
The following code snippet evaluates an expression against a parsed jackson Json node.
```
    final JsonNode root = new ObjectMapper().readTree("{\"val\" : 10 }");
    final String expr = " \"$.val\" + 2 > 9";
    if(engine.evaluate(expr, root)) {
        ...
    }
```

> For real life use cases, rule should be parsed only once and cached. Same cached rule can be evaluated multiple times by passing different payloads. See wiki for more details.

# Documentation
Please go through the hope wiki for detailed documentation

## License
Hope is licensed under Apache License Version 2.0
