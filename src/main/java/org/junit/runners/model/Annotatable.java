package org.junit.runners.model;

import java.lang.annotation.Annotation;

/**
 * A model element that may have annotations.
 * 一个对象元素可能存在的注解.
 * 
 * @since 4.12
 */
public interface Annotatable {
    /**
     * Returns the model elements' annotations.
     * 返回实体元素的注解.
     */
    Annotation[] getAnnotations();

    /**
     * Returns the annotation on the model element of the given type, or @code{null}
     * 返回给定类型的类的注解或者@code{null}
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationType);
}
