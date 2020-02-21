package io.agora.agorauikit.ui;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Iterator;

import io.agora.agorauikit.R;
import io.agora.agorauikit.config.UIConfig;
import io.agora.agorauikit.rtc.AgoraEventHandler;
import io.agora.agorauikit.rtc.EventHandler;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class CallActivity extends Activity implements EventHandler {

    private static final String TAG = CallActivity.class.getSimpleName();

    public static final int LAYOUT_TYPE_DEFAULT = 0;
    public static final int LAYOUT_TYPE_SMALL = 1;

    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>();
    public int mLayoutType = LAYOUT_TYPE_DEFAULT;
    private GridVideoViewContainer mGridVideoViewContainer;
    private RelativeLayout mSmallVideoViewDock;

    private volatile boolean mVideoMuted = false;
    private volatile boolean mAudioMuted = false;
    private volatile boolean mFullScreen = false;

    private boolean mIsLandscape = false;

    private SmallVideoViewAdapter mSmallVideoViewAdapter;

    private final Handler mUIHandler = new Handler();
    private AgoraEventHandler mHandler;
    private RtcEngine mRtcEngine;

    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private String appId;
    private String channelName;
    private String token;
    private Intent mIntent;
    private UIConfig mConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initUIandEvent();
    }

    private void initAgoraEngineAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mHandler);
        } catch (Exception e) {
            Log.e(TAG, "SDK init failed. Please check if you have entered a valid APP ID\n" + Log.getStackTraceString(e));
            showLongToast("SDK init failed. Please check if you have entered a valid APP ID");
            return;
        }

        configVideo();
    }

    private void configVideo() {
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
        ));
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length < 1) {
            return;
        }

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
        }
    }


    protected void initUIandEvent() {
        mHandler = new AgoraEventHandler();
        mHandler.addHandler(this);

        Intent i = getIntent();

        appId = i.getStringExtra("APP_ID");
        channelName = i.getStringExtra("CHANNEL");
        token = i.getStringExtra("TOKEN");
        mConfig = (UIConfig) i.getSerializableExtra("CONFIG");
        mIntent = i.getParcelableExtra(Intent.EXTRA_INTENT);

        if (mConfig == null) {
            mConfig = new UIConfig();
        }

        setButtonColorAndListener(mConfig.getmSwitchCameraBackground(), mConfig.getmSwitchCameraForeground(), R.id.btn_switch_camera);
        setButtonColorAndListener(mConfig.getmAudioMuteBackground(), mConfig.getmAudioMuteForeground(), R.id.btn_mute);
        setButtonColorAndListener(mConfig.getmVideoMuteBackground(), mConfig.getmVideoMuteForeground(), R.id.mute_video);
        setButtonColorAndListener(mConfig.getmCheckBackground(), mConfig.getmCheckForeground(), R.id.check);
        setButtonColorAndListener(mConfig.getmCallBackground(), mConfig.getmCallForeground(), R.id.btn_call);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);

        mGridVideoViewContainer = findViewById(R.id.grid_video_view_container);
        Log.w(TAG, "grid view container is " + mGridVideoViewContainer);
        mGridVideoViewContainer.setItemEventHandler(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                onBigVideoViewClicked(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onItemDoubleClick(View view, int position) {
                onBigVideoViewDoubleClicked(view, position);
            }
        });

        initAgoraEngineAndJoinChannel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, 0));
                surfaceV.setZOrderOnTop(false);
                surfaceV.setZOrderMediaOverlay(false);

                mUidsList.put(0, surfaceV); // get first surface view
            }
        });


        mGridVideoViewContainer.initViewContainer(CallActivity.this, 0, mUidsList, mIsLandscape); // first is now full view
        mRtcEngine.enableVideo();
        mRtcEngine.joinChannel(token, channelName, "", 0);

        optional();
    }

    private void onBigVideoViewClicked(View view, int position) {
        Log.d(TAG, "onItemClick " + view + " " + position + " " + mLayoutType);

        toggleFullscreen();
    }

    private void onBigVideoViewDoubleClicked(View view, int position) {
        Log.d(TAG, "onItemDoubleClick " + view + " " + position + " " + mLayoutType);

        if (mUidsList.size() < 2) {
            return;
        }

        UserStatusData user = mGridVideoViewContainer.getItem(position);
        int uid = user.mUid;

        if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 1) {
            switchToSmallVideoView(uid);
        } else {
            switchToDefaultVideoView();
        }
    }

    private void onSmallVideoViewDoubleClicked(View view, int position) {
        Log.d(TAG, "onItemDoubleClick small " + view + " " + position + " " + mLayoutType);

        switchToDefaultVideoView();
    }

    private void makeActivityContentShownUnderStatusBar() {
        // https://developer.android.com/training/system-ui/status
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            decorView.setSystemUiVisibility(uiOptions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.agora_blue));
            }
        }
    }

    private void showOrHideStatusBar(boolean hide) {
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = decorView.getSystemUiVisibility();

            if (hide) {
                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            } else {
                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            }

            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void setButtonColor(String background, String foreground, ImageButton sb, boolean pressed) {
        if (foreground != null) {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(sb.getDrawable()),
                    Color.parseColor(foreground)
            );
        }

        if (background != null) {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(sb.getBackground()),
                    Color.parseColor(background)
            );
        } else {
            DrawableCompat.setTint(
                    DrawableCompat.wrap(sb.getBackground()),
                    Color.parseColor(pressed ? "#BBBBBB" : "#FFFFFF")
            );
        }
    }

    private void setButtonColorAndListener(final String background, final String foreground, @IdRes final int id) {
        final ImageButton sb = findViewById(id);
        final int offIcon;
        final int onIcon;

        if (id == R.id.btn_mute) {

            if (!mConfig.isAudioMute()) {
                sb.setVisibility(View.GONE);
                return;
            }

            offIcon = mConfig.getMuteAudioPressedIcon();
            onIcon = mConfig.getMuteAudioIcon();
        } else if (id == R.id.btn_switch_camera) {

            if (!mConfig.isSwitchCamera()) {
                sb.setVisibility(View.GONE);
                return;
            }

            offIcon = mConfig.getSwitchCameraPressedIcon();
            onIcon = mConfig.getSwitchCameraIcon();
        } else if (id == R.id.mute_video) {

            if (!mConfig.isVideoMute()) {
                sb.setVisibility(View.GONE);
                return;
            }

            offIcon = mConfig.getMuteVideoPressedIcon();
            onIcon = mConfig.getMuteVideoIcon();
        } else if (id == R.id.check) {

            if (!mConfig.isCheck()) {
                sb.setVisibility(View.GONE);
                return;
            }

            offIcon = mConfig.getCheckIcon();
            onIcon = mConfig.getCheckIcon();
        } else {
            offIcon = mConfig.getCallIcon();
            onIcon = mConfig.getCallIcon();
        }

        sb.setImageDrawable(getResources().getDrawable(onIcon));
        setButtonColor(background, foreground, sb, false);

        if (id == R.id.btn_call) return;

        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setSelected(!sb.isSelected());

                if (sb.isSelected()) {
                    if (id == R.id.btn_switch_camera) {
                        setButtonColor(mConfig.getmSwitchCameraPressedBackground(), mConfig.getmSwitchCameraPressedForeground(), sb, true);
                    } else if (id == R.id.mute_video) {
                        setButtonColor(mConfig.getmVideoMutePressedBackground(), mConfig.getmVideoMutePressedForeground(), sb, true);
                    } else if (id == R.id.btn_mute) {
                        setButtonColor(mConfig.getmAudioMutePressedBackground(), mConfig.getmAudioMutePressedForeground(), sb, true);
                    }
                    sb.setImageDrawable(getResources().getDrawable(offIcon));
                }
                else {
                    sb.setImageDrawable(getResources().getDrawable(onIcon));
                    setButtonColor(background, foreground, sb, false);
                }

                if (id == R.id.btn_switch_camera) {
                    onSwitchCameraClicked();
                } else if (id == R.id.check) {
                    onCheckClicked();
                } else  if (id == R.id.btn_mute) {
                    onLocalAudioMuteClicked();
                } else if (id == R.id.mute_video) {
                    onMuteVideoClicked();
                }
            }
        });
    }

    private void toggleFullscreen() {
        mFullScreen = !mFullScreen;

        showOrHideCtrlViews(mFullScreen);

        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showOrHideStatusBar(mFullScreen);
            }
        }, 200); // action bar fade duration
    }

    private void showOrHideCtrlViews(boolean hide) {
        ActionBar ab = getActionBar();
        if (ab != null) {
            if (hide) {
                ab.hide();
            } else {
                ab.show();
            }
        }

        findViewById(R.id.control_panel).setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    private void optional() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    public void onSwitchCameraClicked() {
        mRtcEngine.switchCamera();
    }

    public void onCheckClicked() {
        if (mIntent != null) {
            startActivity(mIntent);
        }
        else {
            showLongToast("No Event Listener has been bound to this button");
        }
    }

    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        RtcEngine.destroy();
        super.onDestroy();
    }

    protected void deInitUIandEvent() {
        doLeaveChannel();
        mUidsList.clear();
    }

    private void doLeaveChannel() {
        mRtcEngine.leaveChannel();
        mRtcEngine.stopPreview();
    }

    public void onCallClicked(View view) {
        Log.i(TAG, "onHangupClicked " + view);
        finish();
    }

    public void onMuteVideoClicked() {
        Log.i(TAG, "onVoiceChatClicked " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }

        SurfaceView surfaceV = getLocalView();
        if (surfaceV == null || surfaceV.getParent() == null) {
            Log.w(TAG, "onVoiceChatClicked failed" + " " + surfaceV);
            return;
        }

        mVideoMuted = !mVideoMuted;

        if (mVideoMuted) {
            mRtcEngine.enableLocalVideo(false);
        } else {
            mRtcEngine.enableLocalVideo(true);
        }

        hideLocalView(mVideoMuted);
        switchToDefaultVideoView();
    }

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void hideLocalView(boolean hide) {
        int uid = 0;
        doHideTargetView(uid, hide);
    }

    private void doHideTargetView(int targetUid, boolean hide) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            UserStatusData bigBgUser = mGridVideoViewContainer.getItem(0);
            if (bigBgUser.mUid == targetUid) { // big background is target view
                mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
            } else { // find target view in small video view list
                Log.w(TAG, "SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.mUid & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status);
                mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUser.mUid, status, null);
            }
        }
    }

    public void onLocalAudioMuteClicked() {
        Log.i(TAG, "onVoiceMuteClicked " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }

        mRtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);
    }

    private void doRenderRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                if (mUidsList.containsKey(uid)) {
                    return;
                }

                SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                mUidsList.put(uid, surfaceV);
                Log.w(TAG, "TTTTT " + mUidsList);

                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;

                surfaceV.setZOrderOnTop(true);
                surfaceV.setZOrderMediaOverlay(true);

                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

                if (useDefaultLayout) {
                    Log.d(TAG, "doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
                    switchToDefaultVideoView();
                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    Log.d(TAG, "doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.d(TAG, "onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);

        doRenderRemoteUi(uid);
    }

    @Override
    public void onLeaveChannel(IRtcEngineEventHandler.RtcStats stats) {

    }

    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        Log.d(TAG, "onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        Log.d(TAG, "onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);

        doRemoveRemoteUi(uid);
    }

    @Override
    public void onUserJoined(int uid, int elapsed) {

    }

    @Override
    public void onLastmileQuality(int quality) {

    }

    @Override
    public void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result) {

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

    private void requestRemoteStreamType(final int currentHostCount) {
        Log.d(TAG, "requestRemoteStreamType " + currentHostCount);
    }

    private void doRemoveRemoteUi(final int uid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                Object target = mUidsList.remove(uid);

                if (target == null) {
                    return;
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }

                Log.d(TAG, "doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mLayoutType);

                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
                    switchToDefaultVideoView();
                } else {
                    switchToSmallVideoView(bigBgUid);
                }
            }
        });
    }

    private void switchToDefaultVideoView() {
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }
        mGridVideoViewContainer.initViewContainer(this, 0, mUidsList, mIsLandscape);

        mLayoutType = LAYOUT_TYPE_DEFAULT;
        boolean setRemoteUserPriorityFlag = false;
        int sizeLimit = mUidsList.size();
        if (sizeLimit > 5) {
            sizeLimit = 5;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).mUid;
            if (uid != 0) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true;
                    mRtcEngine.setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                    Log.d(TAG, "setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                } else {
                    mRtcEngine.setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORANL);
                    Log.d(TAG, "setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                }
            }
        }
    }

    private void switchToSmallVideoView(int bigBgUid) {
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidsList.get(bigBgUid));
        Iterator<SurfaceView> iterator = mUidsList.values().iterator();
        while (iterator.hasNext()) {
            SurfaceView s = iterator.next();
            s.setZOrderOnTop(true);
            s.setZOrderMediaOverlay(true);
        }

        mUidsList.get(bigBgUid).setZOrderOnTop(false);
        mUidsList.get(bigBgUid).setZOrderMediaOverlay(false);

        mGridVideoViewContainer.initViewContainer(this, bigBgUid, slice, mIsLandscape);

        bindToSmallVideoView(bigBgUid);

        mLayoutType = LAYOUT_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    private void bindToSmallVideoView(int exceptUid) {
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidsList.size() == 2;

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            create = true;
            HashMap<Integer, SurfaceView> mUidsListChanged = new HashMap<Integer, SurfaceView>(mUidsList);
            mUidsListChanged.remove(exceptUid);
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, 0, -1, mUidsListChanged);
            mSmallVideoViewAdapter.setHasStableIds(true);
        }
        recycler.setHasFixedSize(true);

        Log.d(TAG, "bindToSmallVideoView " + twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));

        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recycler.addItemDecoration(new SmallVideoViewDecoration());
        recycler.setAdapter(mSmallVideoViewAdapter);
        recycler.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemDoubleClick(View view, int position) {
                onSmallVideoViewDoubleClicked(view, position);
            }
        }));

        recycler.setDrawingCacheEnabled(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        if (!create) {
            mSmallVideoViewAdapter.setLocalUid(0);
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, null, null);
        }
        for (Integer tempUid : mUidsList.keySet()) {
            if (tempUid != 0) {
                if (tempUid == exceptUid) {
                    mRtcEngine.setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_HIGH);
                    Log.d(TAG, "setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                } else {
                    mRtcEngine.setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_NORANL);
                    Log.d(TAG, "setRemoteUserPriority USER_PRIORITY_NORMAL " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                }
            }
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
            switchToDefaultVideoView();
        } else if (mSmallVideoViewAdapter != null) {
            switchToSmallVideoView(mSmallVideoViewAdapter.getExceptedUid());
        }
    }
}
