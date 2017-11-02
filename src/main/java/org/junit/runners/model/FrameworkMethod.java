package org.junit.runners.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.junit.internal.runners.model.ReflectiveCallable;

/**
 * Represents a method on a test class to be invoked at the appropriate point in
 * test execution. These methods are usually marked with an annotation (such as
 * {@code @Test}, {@code @Before}, {@code @After}, {@code @BeforeClass},
 * {@code @AfterClass}, etc.)
 * 描述在测试执行过程中,一个在测试类运行的方法.
 * @since 4.5
 */
public class FrameworkMethod extends FrameworkMember<FrameworkMethod> {
    private final Method method;

    /**
     * Returns a new {@code FrameworkMethod} for {@code method}
     */
    public FrameworkMethod(Method method) {
        if (method == null) {
            throw new NullPointerException(
                    "FrameworkMethod cannot be created without an underlying method.");
        }
        this.method = method;
    }

    /**
     * Returns the underlying Java method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Returns the result of invoking this method on {@code target} with
     * parameters {@code params}. {@link InvocationTargetException}s thrown are
     * unwrapped, and their causes rethrown.
     */
    public Object invokeExplosively(final Object target, final Object... params)
            throws Throwable {
        //重写被执行方法，再执行用run执行该方法,模板方法.大量应用该模式.
        return new ReflectiveCallable() {
            @Override
            protected Object runReflectiveCall() throws Throwable {
                return method.invoke(target, params);
            }
        }.run();
    }

    /**
     * Returns the method's name
     */
    @Override
    public String getName() {
        return method.getName();
    }

    /**
     * Adds to {@code errors} if this method:
     * 如果这个不符合条件，则添加错误信息到集合中List<Throwable> errors
     * <ul>
     * <li>is not public, or
     * <li>takes parameters, or
     * <li>returns something other than void, or
     * <li>is static (given {@code isStatic is false}), or
     * <li>is not static (given {@code isStatic is true}).
     * </ul>
     */
    public void validatePublicVoidNoArg(boolean isStatic, List<Throwable> errors) {
        validatePublicVoid(isStatic, errors);
        //判断方法不是无参
        if (method.getParameterTypes().length != 0) {
            errors.add(new Exception("Method " + method.getName() + " should have no parameters"));
        }
    }


    /**
     * Adds to {@code errors} if this method:
     * <ul>
     * <li>is not public, or
     * <li>returns something other than void, or
     * <li>is static (given {@code isStatic is false}), or
     * <li>is not static (given {@code isStatic is true}).
     * </ul>
     */
    public void validatePublicVoid(boolean isStatic, List<Throwable> errors) {
        //判断方法不是static
        if (isStatic() != isStatic) {
            //转化成描述文字
            String state = isStatic ? "should" : "should not";
            errors.add(new Exception("Method " + method.getName() + "() " + state + " be static"));
        }
        //判断方法不是public
        if (!isPublic()) {
            errors.add(new Exception("Method " + method.getName() + "() should be public"));
        }
        //判断方法不是void
        if (method.getReturnType() != Void.TYPE) {
            errors.add(new Exception("Method " + method.getName() + "() should be void"));
        }
    }

    @Override
    protected int getModifiers() {
        return method.getModifiers();
    }

    /**
     * Returns the return type of the method
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * Returns the return type of the method
     */
    @Override
    public Class<?> getType() {
        return getReturnType();
    }

    /**
     * Returns the class where the method is actually declared
     * 返回实际声明该方法的类.
     */
    @Override
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    public void validateNoTypeParametersOnArgs(List<Throwable> errors) {
        new NoGenericTypeParametersValidator(method).validate(errors);
    }

    @Override
    public boolean isShadowedBy(FrameworkMethod other) {
        //判断方法是否相同
        //验证名称相同
        if (!other.getName().equals(getName())) {
            return false;
        }
        //验证入参数量相同
        if (other.getParameterTypes().length != getParameterTypes().length) {
            return false;
        }
        //验证入参类型相同
        for (int i = 0; i < other.getParameterTypes().length; i++) {
            if (!other.getParameterTypes()[i].equals(getParameterTypes()[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    boolean isBridgeMethod() {
        return method.isBridge();
    }

    @Override
    public boolean equals(Object obj) {
        //判断类型是否相同
        if (!FrameworkMethod.class.isInstance(obj)) {
            return false;
        }
        return ((FrameworkMethod) obj).method.equals(method);
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    /**
     * Returns true if this is a no-arg method that returns a value assignable
     * to {@code type}
     *
     * @deprecated This is used only by the Theories runner, and does not
     *             use all the generic type info that it ought to. It will be replaced
     *             with a forthcoming ParameterSignature#canAcceptResultOf(FrameworkMethod)
     *             once Theories moves to junit-contrib.
     */
    @Deprecated
    public boolean producesType(Type type) {
        //isAssignableFrom 判断类型是否相同，并是否属于接口或超类
        return getParameterTypes().length == 0 && type instanceof Class<?>
                && ((Class<?>) type).isAssignableFrom(method.getReturnType());
    }

    private Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    /**
     * Returns the annotations on this method
     * 返回该方法的注解数组
     */
    public Annotation[] getAnnotations() {
        return method.getAnnotations();
    }

    /**
     * Returns the annotation of type {@code annotationType} on this method, if
     * one exists.
     * 如果存在，则返回该方法{@code annotationType}类型的注解
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return method.getAnnotation(annotationType);
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
