package cn.linkfeeling.hankserve;

import org.junit.Test;

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
    public void testTime(){
        byte[] u_time=new byte[2];
        u_time[0]=-8;
        u_time[1]=-9;
        System.out.println(HexUtil.encodeHexStr(u_time,false));
    }
}