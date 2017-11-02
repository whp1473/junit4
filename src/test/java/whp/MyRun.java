package whp;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.*;
import sun.security.krb5.internal.crypto.Des;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by wanghouping on 2017/10/27.
 * @author houping wang
 */
public class MyRun extends ParentRunner<FrameworkMethod> {

    private final Object childrenLock = new Object();

    private final ConcurrentMap<FrameworkMethod, Description> methodMap = new ConcurrentHashMap<FrameworkMethod, Description>();

    public MyRun(Class<?> clazz) throws InitializationError {
        super(clazz);
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        return getTestClass().getAnnotatedMethods(Test.class);
    }

    @Override
    protected Description describeChild(FrameworkMethod child) {
        Description description = methodMap.get(child);
        if (description == null) {
            description = Description.createTestDescription(getTestClass().getJavaClass(),
                    testName(child), child.getAnnotations());
            methodMap.putIfAbsent(child, description);
        }
        return description;
    }

    private String testName(FrameworkMethod method) {
        return method.getName();
    }

    @Override
    protected void runChild(FrameworkMethod child, RunNotifier notifier) {

    }

}
