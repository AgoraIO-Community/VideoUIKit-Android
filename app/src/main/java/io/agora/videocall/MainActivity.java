package io.agora.videocall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import io.agora.agorauikit.AgoraVideoCall;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void callStart(View view) {
        String appId = ((EditText) findViewById(R.id.app_id)).getText().toString();
        String channel = ((EditText) findViewById(R.id.channel_name)).getText().toString();

        AgoraVideoCall videoCall = new AgoraVideoCall(getApplicationContext(), appId, null, channel);
        Intent intent = videoCall.start();
        startActivity(intent);
    }
}
