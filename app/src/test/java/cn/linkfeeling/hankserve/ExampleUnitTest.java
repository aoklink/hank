package cn.linkfeeling.hankserve;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.linkfeeling.hankserve.bean.NDKTools;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
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


    @Test
    public void testTime() {

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