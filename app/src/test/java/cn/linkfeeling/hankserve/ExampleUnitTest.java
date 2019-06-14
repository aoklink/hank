package cn.linkfeeling.hankserve;

import org.junit.Test;

import cn.linkfeeling.hankserve.utils.CalculateUtil;

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
        u_time[0]=124;
        u_time[1]=-6;
        int i = CalculateUtil.byteArrayToInt(u_time);
        System.out.println(i);
    }
}