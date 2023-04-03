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

package io.appform.hope.core.values;

import com.google.common.base.Objects;
import com.jayway.jsonpath.JsonPath;
import io.appform.hope.core.Visitor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@ToString(callSuper = true)
public class JsonPathValue extends FunctionEvaluatableValue {

    private final String path;
    private final JsonPath jsonPath;

    /**
     * @param path Json path value
     */
    public JsonPathValue(String path) {
        this.path = path;
        this.jsonPath = JsonPath.compile(path);
    }

    /**
     * @param function A function that evaluates to a json path
     */
    public JsonPathValue(FunctionValue function) {
        super(function);
        this.path = null;
        this.jsonPath = null;
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) { return false; }
        if (obj == this) { return true; }
        if (obj.getClass() != getClass()) { return false; }
        JsonPathValue other = (JsonPathValue) obj;
        return Objects.equal(this.getPath(), other.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getPath());
    }
}
