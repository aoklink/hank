package cn.linkfeeling.hankserve;

import org.junit.Test;

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

        String stringFromNDK = NDKTools.getStringFromNDK();
        System.out.println(stringFromNDK);


    }
}