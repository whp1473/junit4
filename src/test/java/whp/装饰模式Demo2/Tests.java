package whp.装饰模式Demo2;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class Tests {

    @Before
    public void start() {
        System.out.println("之前启动");
    }

    @Test
    public void test() {
        System.out.println("测试项");
    }

    @After
    public void end() {
        System.out.println("之后启动");
    }
}
