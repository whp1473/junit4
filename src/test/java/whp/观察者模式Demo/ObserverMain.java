package whp.观察者模式Demo;

import org.junit.tests.running.methods.InheritedTestTest;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class ObserverMain {

    public static void main(String[] args) {
        Subject subject = new ConcreteSubject();
        subject.update(2);
        Observer a = new ConcreteObserver(subject, "小A");
        Observer b = new ConcreteObserver(subject, "小B");
        Observer c = new ConcreteObserver(subject, "小C");
        subject.update(10);
        subject.update(100);
    }
}
