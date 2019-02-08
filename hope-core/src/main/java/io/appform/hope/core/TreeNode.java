package io.appform.hope.core;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 */
@JsonTypeInfo(include= JsonTypeInfo.As.WRAPPER_OBJECT, use= JsonTypeInfo.Id.NAME)
public abstract class TreeNode {
    public abstract <T> T accept(Visitor<T> visitor);

}
