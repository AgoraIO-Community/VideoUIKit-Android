package io.agora.agorauikit.config;

import java.io.Serializable;

public class UIConfig implements Serializable{
    public boolean mSwitchCamera;
    public boolean mAudioMute;
    public boolean mVideoMute;
    public boolean mCheck;

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

}
