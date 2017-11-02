package whp.装饰模式Demo2;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class TestStatement extends Statement{

    public TestStatement(Class<?> clazz) {
        this.setClazz(clazz);
    }

    @Override
    void run() {
        try {
            Object o = this.getClazz().newInstance();
            Method[] methods = this.getClazz().getMethods();
            for(Method method : methods) {
                Test annotation = method.getAnnotation(Test.class);
                if(null != annotation) {
                    try {
                        method.invoke(o);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
