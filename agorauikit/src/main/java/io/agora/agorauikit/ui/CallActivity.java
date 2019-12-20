package io.agora.agorauikit.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import io.agora.agorauikit.R;
import io.agora.agorauikit.config.UIConfig;
import io.agora.agorauikit.databinding.ActivityCallBinding;
import io.agora.agorauikit.rtc.AgoraEventHandler;
import io.agora.agorauikit.rtc.EventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class CallActivity extends Activity implements EventHandler {
    private VideoGridContainer mVideoGridContainer;
    private RtcEngine rtcEngine;

    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String channelName;
    private String token;
    private AgoraEventHandler mHandler;
    private UIConfig mConfig;
    private String appId;
    private Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mHandler = new AgoraEventHandler();
        mHandler.addHandler(this);

        Intent intent = getIntent();
        appId = intent.getStringExtra("APP_ID");
        channelName = intent.getStringExtra("CHANNEL");
        token = intent.getStringExtra("TOKEN");
        mConfig = (UIConfig) intent.getSerializableExtra("CONFIG");
        mIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);

        if (mConfig == null) {
            mConfig = new UIConfig();
        }

        ActivityCallBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_call);
        binding.setConfig(mConfig);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    private void initAgoraEngineAndJoinChannel() {
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), appId, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        configVideo();
        joinChannel();
        rtcEngine.enableVideo();
        initUI();
    }

    private void configVideo() {
        rtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ));
    }

    private void joinChannel() {
        // Initialize token, extra info here before joining channel
        rtcEngine.joinChannel(token, channelName, "", 0);
    }

    protected SurfaceView prepareRtcVideo(int uid, boolean local) {
        SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
        if (local) {
            rtcEngine.setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, 0));
        } else {
            rtcEngine.setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
        return surface;
    }

    protected void removeRtcVideo(int uid, boolean local) {
        if (local) {
            rtcEngine.setupLocalVideo(null);
        } else {
            rtcEngine.setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rtcEngine.leaveChannel();
        RtcEngine.destroy();
    }

    private void initUI() {
        mVideoGridContainer = findViewById(R.id.live_video_grid_layout);
        startBroadcast();
    }

    private void startBroadcast() {
        SurfaceView surface = prepareRtcVideo(0, true);
        mVideoGridContainer.addUserVideoSurface(0, surface, true);
    }

    private void stopBroadcast() {
        removeRtcVideo(0, true);
        mVideoGridContainer.removeUserVideo(0, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQ_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initAgoraEngineAndJoinChannel();
        }
    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onUserOffline(final int uid, int reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeRemoteUser(uid);
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderRemoteUser(uid);
            }
        });
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    private void renderRemoteUser(int uid) {
        SurfaceView surface = prepareRtcVideo(uid, false);
        mVideoGridContainer.addUserVideoSurface(uid, surface, false);
    }

    private void removeRemoteUser(int uid) {
        removeRtcVideo(uid, false);
        mVideoGridContainer.removeUserVideo(uid, false);
    }

    public void onCallClicked(View view) {
        finish();
    }

    public void onSwitchCameraClicked(View view) {
        rtcEngine.switchCamera();
    }

    public void onMuteVideoClicked(View view) {
        if (!view.isActivated()) {
            stopBroadcast();
        } else {
            startBroadcast();
        }

        ImageView iv = (ImageView) view;

        if (!view.isActivated()) {
            iv.setImageResource(R.drawable.ic_videocam_off_black_24dp);
        }
        else {
            iv.setImageResource(R.drawable.ic_videocam_black_24dp);
        }

        view.setActivated(!view.isActivated());
    }

    public void onLocalAudioMuteClicked(View view) {
        rtcEngine.muteLocalAudioStream(!view.isActivated());

        ImageView iv = (ImageView) view;

        if (!view.isActivated()) {
            iv.setImageResource(R.drawable.btn_mute);
        }
        else {
            iv.setImageResource(R.drawable.btn_unmute);
        }

        view.setActivated(!view.isActivated());
    }

    public void onCheckClicked(View view) {
        if (mIntent != null) {
            startActivity(mIntent);
        }
        else {
            showLongToast("No Event Listener has been bound to this button");
        }
    }

    @Override
    public void onLocalVideoStats(IRtcEngineEventHandler.LocalVideoStats stats) {
    }

    @Override
    public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
    }

    @Override
    public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
    }

    @Override
    public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
    }

    @Override
    public void onRemoteAudioStats(IRtcEngineEventHandler.RemoteAudioStats stats) {
    }

    @Override
    public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {
        // Do nothing at the moment
    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

    }

    @Override
    public void finish() {
        super.finish();
    }
}