package whp;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

/**
 * Created by wanghouping on 2017/10/27.
 * @author houping wang
 */
public class MyRun extends Runner{



    @Override
    public Description getDescription() {
        System.out.println("getDescription()");
        return null;
    }

    @Override
    public void run(RunNotifier notifier) {
        System.out.println("run(RunNotifier notifier)");
    }
}
