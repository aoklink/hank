package cn.linkfeeling.hankserve;

import com.google.gson.Gson;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        Gson gson=new Gson();
        TestClass testClass=new TestClass();
        testClass.setName("ssssss");
        testClass.setHhh(new float[]{1.0f,2.0f});

        System.out.println(gson.toJson(testClass));



    }

    class TestClass{
        private String name;
        private float[] hhh;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float[] getHhh() {
            return hhh;
        }

        public void setHhh(float[] hhh) {
            this.hhh = hhh;
        }
    }
}