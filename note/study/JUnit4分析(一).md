##设计模式角度分析：

###责任链模式

ignoredBuilder()->annotatedBuilder()->suiteMethodBuilder()->junit3Builder()->junit4Builder()会依次执行，
ignoredBuilder()是构造忽略Runner,annotatedBuilder是解析RunWith注解中的Class<?>构造Runner，SuitMethodBuider
是构造一组测试Runner，junit3Builder是构造兼容junit3的Runner，junit4Builder是构造BlockJUnit4ClassRunner，属于
默认的Runner.责任链会依次执行，当遇到符合条件的情况会返回合适的Runner，无合适情况则返回Null.

```java
public Runner runnerForClass(Class<?> testClass) throws Throwable {
    //建造者模式中的指挥者.
    //指挥者本身也是建设者.
    //[设计模式]责任链模式
    List<RunnerBuilder> builders = Arrays.asList(
            //@ignored注解的类或方法将不会被执行
            ignoredBuilder(),
            annotatedBuilder(),
            suiteMethodBuilder(),
            junit3Builder(),
            junit4Builder());

    for (RunnerBuilder each : builders) {
        Runner runner = each.safeRunnerForClass(testClass);
        if (runner != null) {
            return runner;
        }
    }
    return null;
}
```

###模板方法模式

ParentRunner<T>类是BlockJUnit4ClassRunner类的父类，run方法指定类逻辑骨架，而classBlock调用了runChild方法.
ParentRunner<T>类的子类BlockJUnit4ClassRunner类实现了其执行方式.
总结：父类构造骨架，实现通用细节，子类实现特有细节.

ParentRunner<T>类:

```java
@Override
public void run(final RunNotifier notifier) {
    EachTestNotifier testNotifier = new EachTestNotifier(notifier,
            getDescription());
    testNotifier.fireTestSuiteStarted();
    try {
        Statement statement = classBlock(notifier);
        statement.evaluate();
    } catch (AssumptionViolatedException e) {
        testNotifier.addFailedAssumption(e);
    } catch (StoppedByUserException e) {
        throw e;
    } catch (Throwable e) {
        testNotifier.addFailure(e);
    } finally {
        testNotifier.fireTestSuiteFinished();
    }
}

protected abstract void runChild(T child, RunNotifier notifier);
```
Runner类：

```java
@Override
protected void runChild(final FrameworkMethod method, RunNotifier notifier) {
    Description description = describeChild(method);
    if (isIgnored(method)) {
        notifier.fireTestIgnored(description);
    } else {
        Statement statement;
        try {
            statement = methodBlock(method);
        }
        catch (Throwable ex) {
            statement = new Fail(ex);
        }
        runLeaf(statement, description, notifier);
    }
}
```

###观察者模式

观察者模式指的是，观察者订阅主题，主题改变时通知所有观察者.
在Junit中观察者订阅主题，监听方法的开始、执行、完成、异常等，通过外部类RunNotifier
添加监听者，通过内部类SafeNotifier中的run通知所有监听者，执行Listener中的方法.

```java
private abstract class SafeNotifier {
    private final List<RunListener> currentListeners;

    SafeNotifier() {
        this(listeners);
    }

    SafeNotifier(List<RunListener> currentListeners) {
        this.currentListeners = currentListeners;
    }

    void run() {
        int capacity = currentListeners.size();
        List<RunListener> safeListeners = new ArrayList<RunListener>(capacity);
        List<Failure> failures = new ArrayList<Failure>(capacity);
        for (RunListener listener : currentListeners) {
            try {
                notifyListener(listener);
                safeListeners.add(listener);
            } catch (Exception e) {
                failures.add(new Failure(Description.TEST_MECHANISM, e));
            }
        }
        fireTestFailures(safeListeners, failures);
    }

    abstract protected void notifyListener(RunListener each) throws Exception;
}

public void addListener(RunListener listener) {
    if (listener == null) {
        throw new NullPointerException("Cannot add a null listener");
    }
    listeners.add(wrapIfNotThreadSafe(listener));
}
```

这里是真正通知监听器的地方，这个地方是重写了内部类的方法，然后执行run()，
可以看做是模板方法.

```java
public void fireTestRunStarted(final Description description) {
    //模板方法，定义算法骨架，重写实现.
    new SafeNotifier() {
        @Override
        protected void notifyListener(RunListener each) throws Exception {
            each.testRunStarted(description);
        }
    }.run();
}
```

###装饰模式
