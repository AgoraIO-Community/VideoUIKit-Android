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

    /**
     *
     * Constructor for creating the main AgoraVideoCall Object.
     *
     * @param context Pass in the context of the current activity
     * @param appId Pass in the AppId. You can find more here: https://docs.agora.io/en/Agora%20Platform/terms?platform=All%20Platforms#a-nameappidaapp-id
     * @param token Pass in the token string. Recommended for high-security requirements. Enter null for projects that don't have token
     * @param channel A String value for Channel. Two clients will need to be on the same channel to communicate with each other
     *
     *                Examples:
     *                1. With Token
     *                AgoraVideocCall videoCall = new AgoraVideoCall(getApplicationContext(), "randomappid", "token_string", "channelName");
     *
     *                2. Without Token
     *                AgoraVideoCall videoCall = new AgoraVideoCall(getApplicationContext(), "randomappid", null, "channelName");
     */
    public AgoraVideoCall(Context context, String appId, String token, String channel) {
        mContext = context;
        mAppID = appId;
        mToken = token;
        mChannel = channel;
    }


    /**
     *
     * This function will return the intent the user needs to start the video call.
     *
     * @return The video call intent
     *
     * Example:
     *  Intent callIntent = videoCall.start();
     *  startActivity(callIntent); // Start the video call activity
     */
    public Intent start() {
        Intent callIntent = new Intent(mContext, CallActivity.class);
        callIntent.putExtra("APP_ID", mAppID);
        callIntent.putExtra("CHANNEL", mChannel);
        callIntent.putExtra("TOKEN", mToken);
        callIntent.putExtra("CONFIG", mConfig);
        callIntent.putExtra(Intent.EXTRA_INTENT, intent);
        return callIntent;
    }

    /**
     *
     * This function sets which activity will be called if the user taps on the check button
     *
     * @param intent The intent that will start when the user taps on the check button
     */
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     *
     * Sets the config for the video call activity
     *
     * @param config An instance of the UIConfig class which contains all the custom configuration
     *               to add to the video call activity
     */
    public void setConfig(UIConfig config) {
        mConfig = config;
    }
}