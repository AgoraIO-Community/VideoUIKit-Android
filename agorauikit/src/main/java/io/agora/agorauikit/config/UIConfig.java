package io.agora.agorauikit.config;

import java.io.Serializable;

public class UIConfig implements Serializable{
    public boolean mSwitchCamera;
    public boolean mAudioMute;
    public boolean mVideoMute;
    public boolean mCheck;

    private String mSwitchCameraBackground;
    private String mSwitchCameraForeground;
    private String mAudioMuteBackground;
    private String mAudioMuteForeground;
    private String mVideoMuteBackground;
    private String mVideoMuteForeground;
    private String mCheckBackground;
    private String mCheckForeground;

    public boolean isSwitchCamera() {
        return mSwitchCamera;
    }

    public boolean isAudioMute() {
        return mAudioMute;
    }

    public boolean isVideoMute() {
        return mVideoMute;
    }

    public boolean isCheck() {
        return mCheck;
    }

    public UIConfig() {
        mSwitchCamera = true;
        mAudioMute = true;
        mVideoMute = true;
        mCheck = false;
    }

    public UIConfig hideSwitchCamera() {
        mSwitchCamera = false;
        return this;
    }

    public UIConfig hideAudioMute() {
        mAudioMute = false;
        return this;
    }

    public UIConfig hideVideoMute() {
        mVideoMute = false;
        return this;
    }

    public UIConfig showCheckButton() {
        mCheck = true;
        return this;
    }

    public UIConfig setSwitchCameraColor(String background, String foreground) {
        mSwitchCameraBackground = background;
        mSwitchCameraForeground = foreground;
        return this;
    }

    public UIConfig setSwitchCameraBackgroundColor(String background) {
        mSwitchCameraBackground = background;
        return this;
    }

    public UIConfig setSwitchCameraForegroundColor(String foreground) {
        mSwitchCameraForeground = foreground;
        return this;
    }

    public UIConfig setAudioMuteColor(String background, String foreground) {
        mAudioMuteBackground = background;
        mAudioMuteForeground = foreground;
        return this;
    }

    public UIConfig setAudioMuteBackgroundColor(String background) {
        mAudioMuteBackground = background;
        return this;
    }

    public UIConfig setAudioMuteForegroundColor(String foreground) {
        mAudioMuteForeground = foreground;
        return this;
    }

    public UIConfig setVideoMuteColor(String background, String foreground) {
        mVideoMuteBackground = background;
        mVideoMuteForeground = foreground;
        return this;
    }

    public UIConfig setVideoMuteBackgroundColor(String background) {
        mVideoMuteBackground = background;
        return this;
    }

    public UIConfig setVideoMuteForegroundColor(String foreground) {
        mVideoMuteForeground = foreground;
        return this;
    }

    public UIConfig setCheckColor(String background, String foreground) {
        mCheckBackground = background;
        mCheckForeground = foreground;
        return this;
    }

    public UIConfig setCheckBackgroundColor(String background) {
        mCheckBackground = background;
        return this;
    }

    public UIConfig setCheckForegroundColor(String foreground) {
        mCheckForeground = foreground;
        return this;
    }

    public String getmSwitchCameraBackground() {
        return mSwitchCameraBackground;
    }

    public String getmSwitchCameraForeground() {
        return mSwitchCameraForeground;
    }

    public String getmAudioMuteBackground() {
        return mAudioMuteBackground;
    }

    public String getmAudioMuteForeground() {
        return mAudioMuteForeground;
    }

    public String getmVideoMuteBackground() {
        return mVideoMuteBackground;
    }

    public String getmVideoMuteForeground() {
        return mVideoMuteForeground;
    }

    public String getmCheckBackground() {
        return mCheckBackground;
    }

    public String getmCheckForeground() {
        return mCheckForeground;
    }
}
