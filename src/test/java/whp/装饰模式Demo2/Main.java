package whp.装饰模式Demo2;

import org.junit.Test;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class Main {

    public static void main(String[] args) {
        TestStatement test = new TestStatement(Tests.class);
        BeforeStatement beforeStatement = new BeforeStatement(test);
        AfterStatement afterStatement = new AfterStatement(beforeStatement);
        afterStatement.run();
    }
}
