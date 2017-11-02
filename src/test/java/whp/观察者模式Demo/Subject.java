package whp.观察者模式Demo;

/**
 * Created by wanghouping on 2017/10/31.
 * 主题.
 * @author houping wang
 */
public interface Subject {

    /**
     * 注册观察者.
     * @param observer 观察者.
     */
    void registerObserver(Observer observer);

    /**
     * 删除观察者.
     * @param observer 观察者.
     */
    void removeObserver(Observer observer);

    /**
     * 通知观察者.
     */
    void notifyObservers();

    /**
     * 更新消息.
     * @param value value
     */
    void update(int value);

    int getValue();
}
