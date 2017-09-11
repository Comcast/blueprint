package com.xfinity.rmvp_annotations;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Target(value = TYPE)
public @interface DefaultPresenter {
    Class<?> viewClass();

    //TODO if the 'library' project were a straight java project, we could enforce stricter rules on this annotation,
    // i.e. Class<? extends ComponentView> viewClass();
}
