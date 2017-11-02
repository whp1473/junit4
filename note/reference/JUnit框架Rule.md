
JUnit4学习笔记（四）：利用Rule扩展JUnit

    博客分类： Java

JavaJUnit单元测试Rule

一、Rule简介

Rule是JUnit4中的新特性，它让我们可以扩展JUnit的功能，灵活地改变测试方法的行为。JUnit中用@Rule和@ClassRule两个注解来实现Rule扩展，这两个注解需要放在实现了TestRule借口的成员变量（@Rule）或者静态变量（@ClassRule）上。@Rule和@ClassRule的不同点是，@Rule是方法级别的，每个测试方法执行时都会调用被注解的Rule，而@ClassRule是类级别的，在执行一个测试类的时候只会调用一次被注解的Rule



二、JUnit内置Rule

JUnit4中默认实现了一些常用的Rule：



TemporaryFolder Rule

使用这个Rule可以创建一些临时目录或者文件，在一个测试方法结束之后，系统会自动清空他们。


Java代码  收藏代码

    //创建TemporaryFolder Rule
    //可以在构造方法上加入路径参数来指定临时目录，否则使用系统临时目录
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testTempFolderRule() throws IOException {
        //在系统的临时目录下创建文件或者目录，当测试方法执行完毕自动删除
        tempFolder.newFile("test.txt");
        tempFolder.newFolder("test");
    }



ExternalResource Rule

ExternalResource 是TemporaryFolder的父类，主要用于在测试之前创建资源，并在测试完成后销毁。
Java代码  收藏代码

    File tempFile;

    @Rule
    public ExternalResource extResource = new ExternalResource() {
        //每个测试执行之前都会调用该方法创建一个临时文件
        @Override
        protected void before() throws Throwable {
            tempFile = File.createTempFile("test", ".txt");
        }

        //每个测试执行之后都会调用该方法删除临时文件
        @Override
        protected void after() {
            tempFile.delete();
        }
    };

    @Test
    public void testExtResource() throws IOException {
        System.out.println(tempFile.getCanonicalPath());
    }



ErrorCollector Rule

ErrorCollector允许我们收集多个错误，并在测试执行完后一次过显示出来
Java代码  收藏代码

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    @Test
    public void testErrorCollector() {
        errorCollector.addError(new Exception("Test Fail 1"));
        errorCollector.addError(new Throwable("fff"));
    }



Verifier Rule

Verifier是ErrorCollector的父类，可以在测试执行完成之后做一些校验，以验证测试结果是不是正确
Java代码  收藏代码

    String result;

    @Rule
    public Verifier verifier = new Verifier() {
        //当测试执行完之后会调用verify方法验证结果，抛出异常表明测试失败
        @Override
        protected void verify() throws Throwable {
            if (!"Success".equals(result)) {
                throw new Exception("Test Fail.");
            }
        }
    };

    @Test
    public void testVerifier() {
        result = "Fail";
    }



TestWatcher Rule

TestWatcher 定义了五个触发点，分别是测试成功，测试失败，测试开始，测试完成，测试跳过，能让我们在每个触发点执行自定义的逻辑。
Java代码  收藏代码

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            System.out.println(description.getDisplayName() + " Succeed");
        }

        @Override
        protected void failed(Throwable e, Description description) {
            System.out.println(description.getDisplayName() + " Fail");
        }

        @Override
        protected void skipped(AssumptionViolatedException e, Description description) {
            System.out.println(description.getDisplayName() + " Skipped");
        }

        @Override
        protected void starting(Description description) {
            System.out.println(description.getDisplayName() + " Started");
        }

        @Override
        protected void finished(Description description) {
            System.out.println(description.getDisplayName() + " finished");
        }
    };

    @Test
    public void testTestWatcher() {
        /*
            测试执行后会有以下输出：
            testTestWatcher(org.haibin369.test.RulesTest) Started
            Test invoked
            testTestWatcher(org.haibin369.test.RulesTest) Succeed
            testTestWatcher(org.haibin369.test.RulesTest) finished
         */
        System.out.println("Test invoked");
    }



TestName Rule

TestName能让我们在测试中获取目前测试方法的名字。
Java代码  收藏代码

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testTestName() {
        //打印出测试方法的名字testTestName
        System.out.println(testName.getMethodName());
    }



Timeout与ExpectedException Rule

分别用于超时测试与异常测试，在JUnit4学习笔记（一）：基本应用中有提到，这里不再举例。





三、实现原理与部分源码解析

在Junit4的默认Test Runner - org.junit.runners.BlockJUnit4ClassRunner中，有一个methodBlock方法：
Java代码  收藏代码

    protected Statement methodBlock(FrameworkMethod method) {
        Object test;
        try {
            test = new ReflectiveCallable() {
                @Override
                protected Object runReflectiveCall() throws Throwable {
                    return createTest();
                }
            }.run();
        } catch (Throwable e) {
            return new Fail(e);
        }

        Statement statement = methodInvoker(method, test);
        statement = possiblyExpectingExceptions(method, test, statement);
        statement = withPotentialTimeout(method, test, statement);
        statement = withBefores(method, test, statement);
        statement = withAfters(method, test, statement);
        statement = withRules(method, test, statement);
        return statement;
    }



 在JUnit执行每个测试方法之前，methodBlock方法都会被调用，用于把该测试包装成一个Statement。Statement代表一个具体的动作，例如测试方法的执行，Before方法的执行或者Rule的调用，类似于J2EE中的Filter，Statement也使用了责任链模式，将Statement层层包裹，就能形成一个完整的测试，JUnit最后会执行这个Statement。从上面代码可以看到，有以下内容被包装进Statement中：

    1）测试方法的执行；

    2）异常测试，对应于@Test(expected=XXX.class)；

    3）超时测试，对应与@Test(timeout=XXX)；

    4）Before方法，对应于@Before注解的方法；

    5）After方法，对应于@After注解的方法；

    6）Rule的执行。



在Statement中，可以用evaluate方法控制Statement执行的先后顺序，比如Before方法对应的Statement - RunBefores：
Java代码  收藏代码

    public class RunBefores extends Statement {
        private final Statement fNext;

        private final Object fTarget;

        private final List<FrameworkMethod> fBefores;

        public RunBefores(Statement next, List<FrameworkMethod> befores, Object target) {
            fNext = next;
            fBefores = befores;
            fTarget = target;
        }

        @Override
        public void evaluate() throws Throwable {
            for (FrameworkMethod before : fBefores) {
                before.invokeExplosively(fTarget);
            }
            fNext.evaluate();
        }
    }

 在evaluate中，所有Before方法会先被调用，因为Before方法必须要在测试执行之前调用，然后再执行fNext.evaluate()调用下一个Statement。



理解了Statement，再看回Rule的接口org.junit.rules.TestRule：
Java代码  收藏代码

    public interface TestRule {
        Statement apply(Statement base, Description description);
    }

里面只有一个apply方法，用于包裹上级Statement并返回一个新的Statement。因此实现Rule主要是需要实现一个Statement。



四、自定义Rule

通过上面的分析，我们大概知道了如何实现一个Rule，下面是一个例子：
Java代码  收藏代码

    /*
       用于循环执行测试的Rule，在构造函数中给定循环次数。
     */
    public class LoopRule implements TestRule{
        private int loopCount;

        public LoopRule(int loopCount) {
            this.loopCount = loopCount + 1;
        }

        @Override
        public Statement apply(final Statement base, Description description) {
            return new Statement() {
                //在测试方法执行的前后分别打印消息
                @Override
                public void evaluate() throws Throwable {
                    for (int i = 1; i < loopCount; i++) {
                        System.out.println("Loop " + i + " started!");
                        base.evaluate();
                        System.out.println("Loop "+ i + " finished!");
                    }
                }
            };
        }
    }



使用该自定义的Rule：
Java代码  收藏代码

    @Rule
    public LoopRule loopRule = new LoopRule(3);

    @Test
    public void testLoopRule() {
        System.out.println("Test invoked!");
    }



执行后打印出以下信息：
Java代码  收藏代码

    Loop 1 started!
    Test invoked!
    Loop 1 finished!
    Loop 2 started!
    Test invoked!
    Loop 2 finished!
    Loop 3 started!
    Test invoked!