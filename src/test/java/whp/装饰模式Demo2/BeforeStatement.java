package whp.装饰模式Demo2;

import org.junit.After;
import org.junit.Before;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public class BeforeStatement extends Statement{

    private Statement statement;

    public BeforeStatement(Statement statement) {
        this.statement = statement;
        setClazz(statement.getClazz());
    }

    @Override
    void run() {
        Class<?> clazz = statement.getClazz();
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            Before annotation = method.getAnnotation(Before.class);
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
        statement.run();
    }
}
                                                  