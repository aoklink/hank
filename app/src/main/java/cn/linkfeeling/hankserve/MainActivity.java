package cn.linkfeeling.hankserve;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.link.feeling.framework.base.FrameworkBaseActivity;
import com.link.feeling.framework.executor.ThreadPoolManager;
import com.link.feeling.mvp.common.MvpPresenter;

import cn.linkfeeling.link_socketserve.NettyServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                NettyServer.getInstance().bind();
            }
        });


    }
}
