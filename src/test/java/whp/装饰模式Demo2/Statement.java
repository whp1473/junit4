package whp.装饰模式Demo2;

/**
 * Created by wanghouping on 2017/10/31.
 * @author houping wang
 */
public abstract class Statement {

    private Class<?> aClass;

    public void setClazz(Class<?> clazz) {
        this.aClass = clazz;
    }

    public Class<?> getClazz() {
        return this.aClass;
    }

    abstract void run();

}
                                                  