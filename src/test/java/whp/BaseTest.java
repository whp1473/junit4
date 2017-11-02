package whp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Created by wanghouping on 2017/10/26.
 * @author houping wang
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BaseTest{

//    public BaseTest() {
//        System.out.println("构造");
//    }

    @Before
    public void before() {
        System.out.println("开始");
    }

    @Test
    public void run() {
        System.out.println("执行");
    }

//    @After
//    public void after() {
//        System.out.println("结束");
//    }
}
