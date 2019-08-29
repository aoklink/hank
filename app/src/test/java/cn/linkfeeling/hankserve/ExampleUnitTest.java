package cn.linkfeeling.hankserve;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cn.linkfeeling.hankserve.bean.NDKTools;
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

        LimitQueue<Integer> limitQueue = new LimitQueue<>(10);
        limitQueue.offer(1);
        limitQueue.offer(2);
        limitQueue.offer(3);
        limitQueue.offer(4);
        limitQueue.offer(5);
        limitQueue.offer(6);
        limitQueue.offer(7);
        limitQueue.offer(8);
        limitQueue.offer(9);
        limitQueue.offer(10);

        int[] nn = new int[10];
        List<Integer> list = new ArrayList<>(limitQueue);

        for (int i = 0; i < list.size(); i++) {
            nn[i]=list.get(i);
            System.out.println(nn[i]);
        }




    }
}