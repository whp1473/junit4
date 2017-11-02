package whp.装饰模式Demo2;

import org.junit.After;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class AfterStatement extends Statement{

    private Statement statement;

    public AfterStatement(Statement statement) {
        this.statement = statement;
        setClazz(statement.getClazz());
    }

    @Override
    void run() {
        statement.run();
        Class<?> clazz = statement.getClazz();
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            After annotation = method.getAnnotation(After.class);
            if(null != annotation) {
                try {
                    method.invoke(clazz.newInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
