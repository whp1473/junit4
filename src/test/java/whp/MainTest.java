package whp;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Created by wanghouping on 2017/10/26.
 * @author houping wang
 */
public class MainTest {

    public static void main(String[] args) {
        JUnitCore jUnitCore = new JUnitCore();
        Result result = jUnitCore.run(BaseTest.class);
        System.out.println("测试---" +  result.getRunCount());
    }
}
