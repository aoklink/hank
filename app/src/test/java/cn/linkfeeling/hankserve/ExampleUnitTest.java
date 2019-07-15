package cn.linkfeeling.hankserve;

import org.junit.Test;

import cn.linkfeeling.hankserve.queue.LimitQueue;
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
//        byte[] u_time=new byte[1];
//        u_time[0]=64;
//        System.out.println(CalculateUtil.byteToInt((byte) -64));

        float ee = 2.6f;
  //      System.out.println((int)ee);

        LimitQueue<Integer> limitQueue=new LimitQueue<>(1);
        limitQueue.offer(10);
        System.out.println(limitQueue.contains(10));
        System.out.println(limitQueue.contains(11));

    }
}