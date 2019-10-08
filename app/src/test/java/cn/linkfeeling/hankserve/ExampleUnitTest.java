package cn.linkfeeling.hankserve;

import org.junit.Test;

import java.util.Comparator;
import java.util.TreeSet;

import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.HexUtil;

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


    @Test
    public void testTime() {
     /*   byte[] u_time=new byte[2];
        u_time[0]= (byte) 255;
        u_time[1]= (byte) 255;
        System.out.println(CalculateUtil.byteArrayToInt(u_time));*/
        TreeSet<Integer> treeSet = new TreeSet<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                //o2是前面存入的   o1是后面存入的
                int result = o2 - o1;
                short ss = (short) result;
                //    System.out.println(result);
                if (ss <= -1) {
                    return 1;
                }
                return -1;
            }
        });


        treeSet.add(6531);
        treeSet.add(6530);
        treeSet.add(6532);
        treeSet.add(6534);
        treeSet.add(6533);
        treeSet.add(6535);
        treeSet.add(6536);
        treeSet.add(32771);
        treeSet.add(32973);
        treeSet.add(35973);
        treeSet.add(22763);
        treeSet.add(22963);
        treeSet.add(32663);
        treeSet.add(32764);
        treeSet.add(32765);
        treeSet.add(32766);
        treeSet.add(32763);
        treeSet.add(32768);
        treeSet.add(32769);
        treeSet.add(32773);
        treeSet.add(32767);
        treeSet.add(55555);
        treeSet.add(65534);
        treeSet.add(65530);
        treeSet.add(65531);
        treeSet.add(65532);
        treeSet.add(65533);
    /*    treeSet.add(0);
        treeSet.add(2);
        treeSet.add(1);
        treeSet.add(4);
        treeSet.add(3);
        treeSet.add(13);*/

        System.out.println(treeSet);
        short dd = (short)-65534;
        System.out.println(dd);


        TreeSet<XXX> treeSet1 = new TreeSet<>(new Comparator<XXX>() {
            @Override
            public int compare(XXX o1, XXX o2) {
                int seq = o1.getSeq();
                int seq1 = o2.getSeq();
                if (seq1 - seq <= -1) {
                    return 1;

                }
                return -1;
            }
        });

        treeSet1.add(new XXX(1,6553));
        treeSet1.add(new XXX(2,6552));
        treeSet1.add(new XXX(3,6554));
        treeSet1.add(new XXX(4,6555));
        treeSet1.add(new XXX(5,6550));
        treeSet1.add(new XXX(6,1));
        treeSet1.add(new XXX(7,0));

        for (XXX xxx : treeSet1) {
            System.out.println(xxx.getAge()+"---"+xxx.getSeq());
        }


    }

    class XXX {
        private int age;
        private int seq;

        public XXX(int age, int seq) {
            this.age = age;
            this.seq = seq;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }
    }


}

