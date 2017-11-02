package whp.装饰模式Demo;

/**
 * Created by wanghouping on 2017/10/31.
 */
public class Bird extends Change{

    public Bird(TheGreatestSage sage) {
        super(sage);
    }

    @Override
    public void move() {
        sage.move();
        System.out.println("Bird Move");
    }
}
