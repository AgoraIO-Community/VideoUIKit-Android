package io.agora.agorauikit;

import android.content.Context;
import android.content.Intent;

import io.agora.agorauikit.config.UIConfig;
import io.agora.agorauikit.ui.CallActivity;

public class AgoraVideoCall {
    private final Context mContext;
    private final String mAppID;
    private final String mToken;
    private final String mChannel;
    private UIConfig mConfig;
    private Intent intent;

    public AgoraVideoCall(Context context, String appId, String token, String channel) {
        mContext = context;
        mAppID = appId;
        mToken = token;
        mChannel = channel;
    }

    public Intent start() {
        Intent callIntent = new Intent(mContext, CallActivity.class);
        callIntent.putExtra("APP_ID", mAppID);
        callIntent.putExtra("CHANNEL", mChannel);
        callIntent.putExtra("TOKEN", mToken);
        callIntent.putExtra("CONFIG", mConfig);
        callIntent.putExtra(Intent.EXTRA_INTENT, intent);
        return callIntent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public void setConfig(UIConfig config) {
        mConfig = config;
    }
}