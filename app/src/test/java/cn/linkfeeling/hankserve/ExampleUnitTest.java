package cn.linkfeeling.hankserve;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.NDKTools;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.HexUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


    class Pe{
        private int age;
        private String name;

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

/*        @Override
        public int hashCode() {
            return new Integer(age).hashCode();
        }

        @Override
        public boolean equals( Object obj) {
            if (this == obj) {
                return true;//地址相等
            }

            if (obj == null) {
                return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
            }

            if (obj instanceof Pe) {
                Pe other = (Pe) obj;
                //需要比较的字段相等，则这两个对象相等
                if (this.getAge()==other.getAge()) {
                    return true;
                }
            }

            return false;
        }*/
    }


    @Test
    public void testTime() {

        HashMap<String,Pe> map=new HashMap<>();

        Pe p2=new Pe();
        p2.setAge(14);
        p2.setName("小明");
        map.put(p2.getName(),p2);

        Pe p1=new Pe();
        p1.setAge(11);
        p1.setName("小明");
        map.put(p1.getName(),p1);



        Iterator<Map.Entry<String, Pe>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Pe> next = iterator.next();
            Pe value = next.getValue();
            System.out.println(value.getAge()+"---"+value.getName());
        }


        List<String> webAccounts =new ArrayList<>();
        webAccounts.add("1111");
        webAccounts.add("2222");
        webAccounts.add("3333");
        webAccounts.add("4444");
        System.out.println(webAccounts.contains("2121"));


        ByteBuf byteBuf= Unpooled.buffer();

        byteBuf.writeByte(11);
        byteBuf.writeByte(11);
        byteBuf.writeByte(11);
        byteBuf.writeByte(11);


        System.out.println(byteBuf.readerIndex());
        System.out.println(byteBuf.readByte());

        System.out.println(byteBuf.writerIndex());




        byteBuf.writeByte(11);
        byteBuf.writeByte(12);
        byteBuf.writeByte(13);
        System.out.println(byteBuf.readerIndex()+"");  //默认为0
        System.out.println(byteBuf.readableBytes()+"");
        System.out.println(Arrays.toString(byteBuf.array()));
        System.out.println(byteBuf.array().length); //256

    //    byteBuf.readByte();

        System.out.println(byteBuf.readerIndex()+"");
        System.out.println(byteBuf.writerIndex());

        byteBuf.skipBytes(1);
        System.out.println(byteBuf.readerIndex()+"");

        byteBuf.markReaderIndex();

        byteBuf.readByte();

       byteBuf.resetReaderIndex();
        System.out.println(byteBuf.readerIndex());





    }
}