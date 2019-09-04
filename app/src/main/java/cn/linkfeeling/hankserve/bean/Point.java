package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/8/14
 */
public class Point {

    private int id;
    private double x;
    private double y;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;//地址相等
        }

        if (obj == null) {
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if (obj instanceof Point) {
            Point other = (Point) obj;
            //需要比较的字段相等，则这两个对象相等
            if (this.getId() == other.getId()) {
                return true;
            }
        }

        return false;
    }
}
