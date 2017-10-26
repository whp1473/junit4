package whp;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * Created by wanghouping on 2017/10/26.
 * @author houping wang
 */
@RunWith(Theories.class)
public class ThoriesTest {

    @DataPoint
    public static String _name = "我是谁";

    @DataPoint
    public static String _runs = "11";

    @Theory
    public void test(String name, String run) {
        System.out.println("name:" + name + ",run:" + run);
    }
}
