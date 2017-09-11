package com.xfinity.rmvp_annotations;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target(value = TYPE)
public @interface ComponentViewHolder {
    int viewType();
}
