package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/3/14
 * 手环类
 */
public class Wristband {

    private String bracelet_id;  //name作为手环的id


    public Wristband(String bracelet_id) {
        this.bracelet_id = bracelet_id;

    }

    public String getBracelet_id() {
        return bracelet_id;
    }

    public void setBracelet_id(String bracelet_id) {
        this.bracelet_id = bracelet_id;
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

        if (obj instanceof Wristband) {
            Wristband other = (Wristband) obj;
            //需要比较的字段相等，则这两个对象相等
            if (this.getBracelet_id().equals(other.getBracelet_id())) {
                return true;
            }
        }

        return false;
    }
}
