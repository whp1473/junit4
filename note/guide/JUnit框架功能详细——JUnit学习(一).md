##JUnit框架功能详细——JUnit学习（一）

是著名的单元测试框架，在JUnit4中所有的测试用例采用@Annotation标注，这比JUnit3的通过类继承和特定方法名带来更大的灵活性。在花了一周多的时间学习JUnit4使用及原码后发现自己以往只用到了JUnit的基本功能，忽视了JUnit的很多强大功能。这里记录和分享下自己发现的JUnit新大陆（这些特性可能很多朋友都用过了：>），后面系列的文章会介绍自己学习JUnit原码过程中发现的精彩之处。

一个简单的JUnit
首先定义一个Person的接口用于描述人的若干行为，下面展开针对Person的测试。接口定义如下，定义的行为包括获取年龄、获取名字、与之交谈和走路。

```java
public interface Person
{

	public int getAge();

	public String getName();

	public String talkTo(String message);

	public void walk();
}
```

使用@Test标签标注测试方法，建立一个简单的测试Person各个行为的用例，这里省略PersonImpl的实现代码。JUnit代码如下：

```java
public class PersonTest
{

	private Person person;

	@Before
	public void buildPerson()
	{
		person = new PersonImpl("Willard", 3);
	}

	@Test
	public void testGetAge()
	{
		int age = person.getAge();
		assertEquals(3, age);
	}
}
```

这里分别测试了Person的getAge、getName、talkTo和walk行为，为了节约代码只展示关于getAge的测试用例。使用@Test标注需要执行的测试方法。
限时，测试方法效率。在上面测试中我们验证了Person的基本能力，但是我们要求Person不仅要走而且要走的快——测试walk方法是否能在给定的时间内完成，这里我们使用@Test注解的timeout属性，单位是毫秒。代码如下：

```java
@Test(timeout=200)
public void testWalk()
{
	person.walk();
}
```

异常测试，验证方法能否抛出预期的错误。现在要测试Person的Talk行为除了以“Hello”开始的入参以外都要拒绝对方抛出IllegalArgumentException，使用@Test的expected属性可以完成异常测试的需求，接受异常的class。代码如下：

```java
@Test(expected=IllegalArgumentException.class)
public void testTalk()
{
	String message = person.talkTo("Jimy");
	assertNotNull(message);
}
```

通过expected属性可以指定抛出预期的异常类。除此以外JUnit还可针对异常信息（message）匹配甚至自定义匹配规则，下一篇文章JUnit学习(二)中将在@Rule注解部分介绍这个功能。
JUnit的生命周期
在前面的用例中使用了@Before注解用来创建Person实例，JUnit还提供了@BeforeClass、@After、@AfterClass注解，这些方法的执行顺序是怎样的？通过下面的代码来演示JUnit的生命周期：

```java
public class LifeCycleTest
{

	public LifeCycleTest()
	{
		super();
		System.out.println("<<Person Constructor>>");
	}

	@BeforeClass
	public static void beforeClassM()
	{
		System.out.println("<<Before Class>>");
	}


	@Before
	public void beforeM()
	{
		System.out.println("<<Before>>");
	}


	@AfterClass
	public static void afterClassM()
	{
		System.out.println("<<After Class>>");
	}


	@After
	public void after()
	{
		System.out.println("<<After>>");
	}

	@Test
	public void testMethod1()
	{
		System.out.println("Test Method 1.");
	}

	@Test
	public void testMethod2()
	{
		System.out.println("Test Method 2.");
	}
}
```

上述代码运行后打印结果如下：

`
<<Before Class>>
<<Person Constructor>>
<<Before>>
Test Method 1.
<<After>>
<<Person Constructor>>
<<Before>>
Test Method 2.
<<After>>
<<After Class>>
`

@BeforeClass：修饰static的方法，在整个类执行之前执行该方法一次。比如你的测试用例执行前需要一些高开销的资源（连接数据库）可以用@BeforeClass搞定。值得注意的是如果测试用例类的父类中也存在@BeforeClass修饰的方法，它将在子类的@BeforeClass之前执行。
@AfterClass：同样修饰static的方法，在整个类执行结束前执行一次。如果你用@BeforeClass创建了一些资源现在是时候释放它们了。
@Before：修饰public void的方法，在每个测试用例（方法）执行时都会执行。
@After：修饰public void的方法，在每个测试用例执行结束后执行。
Constructor：每个测试用例都会重新创建当前的Class实例，可以看到Constructor执行了两次。
使用@RunWith注解
Runner：Runner是一个抽象类，是JUnit的核心组成部分。用于运行测试和通知Notifier运行的结果。JUnit使用@RunWith注解标注选用的Runner，由此实现不同测试行为。
BlockJUnit4ClassRunner：这个是JUnit的默认Runner，平时我们编写的JUnit不添加@RunWith注解时使用的都是这个Runner。
Suit：没错，Suit就是个Runner！用来执行分布在多个类中的测试用例，比如我存在SimpleFunctionTest和ComplexFunctionTest类分别测试Person的简单和复杂行为，在茫茫的测试用例中如何一次执行所有与Person有关的测试呢——使用Suit。代码如下：其中ComplexFunctionTest和SimpleFunctionTest就是两个普通的测试用例类，这里忽略。

```java
@RunWith(Suite.class)
@SuiteClasses({ComplexFunctionTest.class, SimpleFunctionTest.class})
public class TestSuitMain
{

}
```

在执行TestSuitMain --> “Run As JUnit Test"的时候会把ComplexFunctionTest和SimpleFunctionTest的用例全部执行一遍。
Parameterized：Parameterized继承自Suit，从这个身世和名字应该可以猜到一些因果了。Parameterized是在参数上实现了Suit——修饰一个测试类，但是可以提供多组构造函数的参数用于测试不同场景。略微有点抽象，用代码说话：

```
@RunWith(Parameterized.class)
public class TestGenerateParams
{

	private String greeting;

	public TestGenerateParams(String greeting)
	{
		super();
		this.greeting = greeting;
	}

	@Test
	public void testParams()
	{
		System.out.println(greeting);
	}

	/**
	 * 这里的返回至少是二维数组
	 * @return
	 */
	@Parameters
	public static List<String[]> getParams()
	{
		return
				Arrays.asList(new String[][]{{"hello"},
						{"hi"},
						{"good morning"},
						{"how are you"}});
	}
}
```

输出结果：
`
hello
hi
good morning
how are you
`

在这个用例里，我们首先需要用@RunWith(Parameterized.class)来修饰我们的测试类；接下来提供一组参数，还记得JUnit的生命周期吗？在每次运行测试方法的时候都会调用Constructor来创建一个实例，这里参数就是通过Constructor的参数传入的。因此如你所见我们需要一个含有参数的构造函数用于接收参数，这个参数需要用于跑测试用例所以把它保存做类的变量；然后用@Parameters修饰我们提供参数的静态方法，它需要返回List<Object[]>，List包含的是参数组，Object[]即按顺序提供的一组参数。
Category：Category同样继承自Suit，Category似乎是Suit的加强版，它和Suit一样提供了将若干测试用例类组织成一组的能力，除此以外它可以对各个测试用例进行分组，使你有机会只选择需要的部分用例。举个例子Person有获取age和name的方法也有talk和walk方法，前者用于获取属性后者是Person的行为，Category使我们可以只运行属性测试，反之亦然。
首先修改最初的测试用例PersonTest，添加Category信息，代码如下在每个用例上添加了@Category信息标识它们是用作Attribute还是Behavior的测试，这不会影响原有用例测运行。

```java
@Category(AttributeFun.class)
@Test
public void testGetAge()
{
	int age = person.getAge();
	assertEquals(3, age);
}

@Category(AttributeFun.class)
@Test
public void testGetName()
{
	String name = person.getName();
	assertEquals("Willard", name);
}

@Category(BehaviorFun.class)
@Test
public void testTalk()
{
	String message = person.talkTo("Jimy");
	assertNotNull(message);
}

@Category(BehaviorFun.class)
@Test(timeout=200)
public void testWalk()
{
	person.walk();
}
```

接下来编写我们的Category测试类，代码如下：

```java
@RunWith(Categories.class)
@SuiteClasses(PersonTest.class)
public class CategoryTest
{

}
```

Runner选用Categories，SuitClass使用PersonTest.class，这时Categories与Suit拥有完全一致的效果。运行截图如下，四个用例都会运行。

接下我们修改代码如下。

```java
@RunWith(Categories.class)
@SuiteClasses(PersonTest.class)
@IncludeCategory(AttributeFun.class)
public class CategoryTest
{

}
```

增加了@IncludeCategory限制只运行AttributeFun的用例，运行截图如下。同样Category还支持@ExcludeCategory注解用于排除用例，用法一致，这里从略。

Theories：意为原理或者推测的意思，我觉得这里应该是取推测。Theories继承自BlockJUnit4ClassRunner，提供了除Parameterized之外的另一种参数测试解决方案——似乎更强大。Theories不再需要使用带有参数的Constructor而是接受有参的测试方法，修饰的注解也从@Test变成了@Theory，而参数的提供则变成了使用@DataPoint或者@Datapoints来修饰的变量，两者的唯一不同是前者代表一个数据后者代表一组数据。Theories会尝试所有类型匹配的参数作为测试方法的入参（有点排列组合的意思）。看一个使用Theories的例子：

```java
@RunWith(Theories.class)
public class TheoriesTest
{

	@DataPoint
	public static String nameValue1 = "Tony";

	@DataPoint
	public static String nameValue2 = "Jim";

	@DataPoint
	public static int ageValue1 = 10;

	@DataPoint
	public static int ageValue2 = 20;

	@Theory
	public void testMethod(String name, int age)
	{
		System.out.println(String.format("%s's age is %s", name, age));
	}
}
```

上面的例子打印结果如下：
`
Tony's age is 10
Tony's age is 20
Jim's age is 10
Jim's age is 20
`

同样使用@DataPoints可以获得一样的效果：

```java
@RunWith(Theories.class)
public class TheoriesTest
{

	@DataPoints
	public static String[] names = {"Tony", "Jim"};

	@DataPoints
	public static int[] ageValue1 = {10, 20};

	@Theory
	public void testMethod(String name, int age)
	{
		System.out.println(String.format("%s's age is %s", name, age));
	}
}
```

除此以外Theories还可以支持自定义数据提供的方式，需要继承JUnit的ParameterSupplier类。下面的代码使用ParameterSupplier实现上述例子：

```java
public class NameSupplier extends ParameterSupplier
{

	@Override
	public List<PotentialAssignment> getValueSources(ParameterSignature sig)
	{
		PotentialAssignment nameAssignment1 = PotentialAssignment.forValue("name", "Tony");
		PotentialAssignment nameAssignment2 = PotentialAssignment.forValue("name", "Jim");
		return Arrays.asList(new PotentialAssignment[]{nameAssignment1, nameAssignment2});
	}

};
```

```java
public class AgeSupplier extends ParameterSupplier
{
	@Override
	public List<PotentialAssignment> getValueSources(ParameterSignature sig)
	{
		PotentialAssignment ageAssignment1 = PotentialAssignment.forValue("age", 10);
		PotentialAssignment ageAssignment2 = PotentialAssignment.forValue("age", 20);
		return Arrays.asList(new PotentialAssignment[]{ageAssignment1, ageAssignment2});
	}
};

@RunWith(Theories.class)
public class TheoriesTest
{

	@Theory
	public void testMethod(@ParametersSuppliedBy(NameSupplier.class)String name, @ParametersSuppliedBy(AgeSupplier.class)int age)
	{
		System.out.println(String.format("%s's age is %s", name, age));
	}
}
```

在此我们介绍了JUnit常用的几个Runner，还有部分Runner与用例编写关系不是很大，不再赘述。
