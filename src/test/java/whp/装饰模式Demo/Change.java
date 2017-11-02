package whp.装饰模式Demo;

/**
 * Created by wanghouping on 2017/10/31.
 */
public class Change implements TheGreatestSage{

    protected TheGreatestSage sage;

    public Change(TheGreatestSage sage){
        this.sage = sage;
    }

    public void move() {
        sage.move();
    }
}
