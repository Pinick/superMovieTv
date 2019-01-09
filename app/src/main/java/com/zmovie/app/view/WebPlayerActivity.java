package com.zmovie.app.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bftv.myapplication.config.KeyParam;
import com.just.agentweb.AgentWeb;
import com.zmovie.app.R;

/**
 * 手机端用吧，TV没必要
 */
public class WebPlayerActivity extends Activity {

    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_player);

        FrameLayout root = findViewById(R.id.root);

        String url = getIntent().getStringExtra(KeyParam.PLAYURL);
        if (TextUtils.isEmpty(url)){
            Toast.makeText(this, "播放地址为空", Toast.LENGTH_SHORT).show();
           return;
        }
        Log.e("testplayurl",url);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((FrameLayout) root, new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }
}
