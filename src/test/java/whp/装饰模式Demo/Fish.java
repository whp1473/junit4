package whp.装饰模式Demo;

/**
 * Created by wanghouping on 2017/10/31.
 */
public class Fish extends Change{

    public Fish(TheGreatestSage sage) {
        super(sage);
    }

    @Override
    public void move() {
        // 代码
        sage.move();
        System.out.println("Fish Move");
    }
}
