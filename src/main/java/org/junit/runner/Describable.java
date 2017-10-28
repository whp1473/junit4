package org.junit.runner;


/**
 * Represents an object that can describe itself
 * 代表一个对象可以描述自身.
 *
 * @since 4.5
 */
public interface Describable {
    /**
     * @return a {@link Description} showing the tests to be run by the receiver
     * 一个{@link Description}描述被接收方运行的测试
     */
    Description getDescription();
}