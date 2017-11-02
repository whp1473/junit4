package whp.观察者模式Demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class ConcreteSubject implements Subject{

    private final List<Observer> list = new ArrayList<Observer>();

    private int value;

    public void registerObserver(Observer observer) {
        list.add(observer);
    }

    public void removeObserver(Observer observer) {
        list.remove(observer);
    }

    public void notifyObservers() {
        for(Observer observer : list) {
            observer.update(value);
        }
    }

    public void update(int value) {
        this.value = value;
        notifyObservers();
    }

    public int getValue() {
        return value;
    }
}
