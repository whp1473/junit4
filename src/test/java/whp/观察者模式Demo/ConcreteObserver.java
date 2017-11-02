package whp.观察者模式Demo;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class ConcreteObserver implements Observer{

    private int old = 0;

    private int value = 0;

    private Subject subject;

    private String name = "";

    public ConcreteObserver(Subject subject, String name) {
        this.name = name;
        this.subject = subject;
        this.old = subject.getValue();
        this.value = subject.getValue();
        subject.registerObserver(this);
    }

    public void update(int value) {
        this.old = this.value;
        this.value = value;
        call();
    }

    private void call(){
        System.out.println(String.format("%s报告数值由%s改变为%s", name , old , value));
    }
}
