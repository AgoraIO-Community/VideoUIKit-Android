package io.agora.agorauikit.config;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

import io.agora.agorauikit.R;

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
    private String mCallBackground;
    private String mCallForeground;

    private String mSwitchCameraPressedBackground;
    private String mSwitchCameraPressedForeground;
    private String mAudioMutePressedBackground;
    private String mAudioMutePressedForeground;
    private String mVideoMutePressedBackground;
    private String mVideoMutePressedForeground;

    private int switchCameraIcon;
    private int switchCameraPressedIcon;
    private int muteAudioIcon;
    private int muteAudioPressedIcon;
    private int muteVideoIcon;
    private int muteVideoPressedIcon;
    private int checkIcon;
    private int callIcon;

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
        muteVideoPressedIcon = R.drawable.ic_videocam_off_black_24dp;
        muteAudioPressedIcon = R.drawable.ic_mic_off_black_24dp;
        muteAudioIcon = R.drawable.ic_mic_black_24dp;
        muteVideoIcon = R.drawable.ic_videocam_black_24dp;
        switchCameraPressedIcon = R.drawable.ic_switch_camera_black_24dp;
        switchCameraIcon = R.drawable.ic_switch_camera_black_24dp;
        checkIcon = R.drawable.ic_check_black_24dp;
        callIcon = R.drawable.ic_call_black_24dp;
    }

    /**
     * Hides the Switch Camera Button from view
     */
    public UIConfig hideSwitchCamera() {
        mSwitchCamera = false;
        return this;
    }

    /**
     * Hides the Switch Camera Button from view
     */
    public UIConfig hideAudioMute() {
        mAudioMute = false;
        return this;
    }

    /**
     * Hides the Mute Video Button from view
     */
    public UIConfig hideVideoMute() {
        mVideoMute = false;
        return this;
    }

    /**
     * Shows the check mark button in the view.
     * It can be used by the user to map it to any intent they want.
     */
    public UIConfig showCheckButton() {
        mCheck = true;
        return this;
    }

    /**
     * Sets the background and foreground color of the Switch Camera Button.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setSwitchCameraColor("#00FFFF", "#FF0000")
     */
    public UIConfig setSwitchCameraColor(String background, String foreground) {
        mSwitchCameraBackground = background;
        mSwitchCameraForeground = foreground;
        return this;
    }

    /**
     * Sets the background color of the Switch Camera Button.
     * @param background Hex code for the background color
     *
     *                   Example: config.setSwitchCameraBackgroundColor("#00FFFF")
     */
    public UIConfig setSwitchCameraBackgroundColor(String background) {
        mSwitchCameraBackground = background;
        return this;
    }

    /**
     * Sets the foreground color of the Switch Camera Button.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setSwitchCameraForegroundColor("#FF0000")
     */
    public UIConfig setSwitchCameraForegroundColor(String foreground) {
        mSwitchCameraForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Mute Audio Button.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setAudioMuteColor("#00FFFF", "#FF0000")
     */
    public UIConfig setAudioMuteColor(String background, String foreground) {
        mAudioMuteBackground = background;
        mAudioMuteForeground = foreground;
        return this;
    }

    /**
     * Sets the background color of the Mute Audio Button.
     * @param background Hex code for the background color
     *
     *                   Example: config.setAudioMuteBackgroundColor("#00FFFF")
     */
    public UIConfig setAudioMuteBackgroundColor(String background) {
        mAudioMuteBackground = background;
        return this;
    }

    /**
     * Sets the foreground color of the Mute Audio Button.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setAudioMuteForegroundColor("#FF0000")
     */
    public UIConfig setAudioMuteForegroundColor(String foreground) {
        mAudioMuteForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Mute Video Button.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setVideoMuteColor("#00FFFF", "#FF0000")
     */
    public UIConfig setVideoMuteColor(String background, String foreground) {
        mVideoMuteBackground = background;
        mVideoMuteForeground = foreground;
        return this;
    }

    /**
     * Sets the background color of the Mute Video Button.
     * @param background Hex code for the background color
     *
     *                   Example: config.setVideoMuteBackgroundColor("#00FFFF")
     */
    public UIConfig setVideoMuteBackgroundColor(String background) {
        mVideoMuteBackground = background;
        return this;
    }

    /**
     * Sets the foreground color of the Mute Video Button.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setVideoMuteForegroundColor("#FF0000")
     */
    public UIConfig setVideoMuteForegroundColor(String foreground) {
        mVideoMuteForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Check Button.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setCheckColor("#00FFFF", "#FF0000")
     */
    public UIConfig setCheckColor(String background, String foreground) {
        mCheckBackground = background;
        mCheckForeground = foreground;
        return this;
    }

    /**
     * Sets the background color of the Check Button.
     * @param background Hex code for the background color
     *
     *                   Example: config.setCheckBackgroundColor("#00FFFF", "#FF0000")
     */
    public UIConfig setCheckBackgroundColor(String background) {
        mCheckBackground = background;
        return this;
    }

    /**
     * Sets the background and foreground color of the Check Button.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setCheckForegroundColor("#00FFFF", "#FF0000")
     */
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

    public int getSwitchCameraIcon() {
        return switchCameraIcon;
    }

    /**
     * This function is used to change the icon for the switch camera button when it is not selected
     * @param switchCameraIcon An Id resource value of the drawable icon
     */
    public UIConfig setSwitchCameraIcon(@DrawableRes int switchCameraIcon) {
        this.switchCameraIcon = switchCameraIcon;
        return this;
    }

    public int getSwitchCameraPressedIcon() {
        return switchCameraPressedIcon;
    }


    /**
     * This function is used to change the icon for the switch camera button when it is selected
     * @param switchCameraPressedIcon An Id resource value of the drawable icon
     */
    public UIConfig setSwitchCameraPressedIcon(@DrawableRes int switchCameraPressedIcon) {
        this.switchCameraPressedIcon = switchCameraPressedIcon;
        return this;
    }

    public int getMuteAudioIcon() {
        return muteAudioIcon;
    }


    /**
     * This function is used to change the icon for the Mute Audio button when it is not selected
     * @param muteAudioIcon An Id resource value of the drawable icon
     */
    public UIConfig setMuteAudioIcon(@DrawableRes int muteAudioIcon) {
        this.muteAudioIcon = muteAudioIcon;
        return this;
    }

    public int getMuteAudioPressedIcon() {
        return muteAudioPressedIcon;
    }

    /**
     * This function is used to change the icon for the Mute Audio button when it is selected
     * @param muteAudioPressedIcon An Id resource value of the drawable icon
     */
    public UIConfig setMuteAudioPressedIcon(@DrawableRes int muteAudioPressedIcon) {
        this.muteAudioPressedIcon = muteAudioPressedIcon;
        return this;
    }

    public int getMuteVideoIcon() {
        return muteVideoIcon;
    }

    /**
     * This function is used to change the icon for the Mute Video button when it is not selected
     * @param muteVideoIcon An Id resource value of the drawable icon
     */
    public UIConfig setMuteVideoIcon(@DrawableRes int muteVideoIcon) {
        this.muteVideoIcon = muteVideoIcon;
        return this;
    }

    public int getMuteVideoPressedIcon() {
        return muteVideoPressedIcon;
    }

    /**
     * This function is used to change the icon for the Mute Video button when it is selected
     * @param muteVideoPressedIcon An Id resource value of the drawable icon
     */
    public UIConfig setMuteVideoPressedIcon(@DrawableRes int muteVideoPressedIcon) {
        this.muteVideoPressedIcon = muteVideoPressedIcon;
        return this;
    }

    public int getCheckIcon() {
        return checkIcon;
    }

    /**
     * This function is used to change the icon for the Check button.
     * Check Icon does not have a separate function for when it is selected since tapping on this button should launch a new Activity.
     * @param checkIcon An Id resource value of the drawable icon
     */
    public UIConfig setCheckIcon(@DrawableRes int checkIcon) {
        this.checkIcon = checkIcon;
        return this;
    }

    public int getCallIcon() {
        return callIcon;
    }

    /**
     * This function is used to change the icon for the End Call button when it is selected.
     * End call Icon does not have a separate function for when it is selected since tapping on this button should end the call activity.
     * @param callIcon An Id resource value of the drawable icon
     */
    public UIConfig setCallIcon(@DrawableRes int callIcon) {
        this.callIcon = callIcon;
        return this;
    }

    public String getmCallBackground() {
        if (mCallBackground == null) {
            return "#FF0000";
        }
        return mCallBackground;
    }

    /**
     * Sets the background color of the Check Button.
     * @param background Hex code for the background color
     *
     *                   Example: config.setCallBackgroundColor("#00FFFF")
     */
    public UIConfig setCallBackgroundColor(String background) {
        this.mCallBackground = background;
        return this;
    }

    public String getmCallForeground() {
        return mCallForeground;
    }

    /**
     * Sets the foreground color of the Call Button.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setCallForegroundColor("#FF0000")
     */
    public UIConfig setCallForegroundColor(String foreground) {
        this.mCallForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Call Button.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setCallColor("#00FFFF", "#FF0000")
     */
    public UIConfig setCallColor(String background, String foreground) {
        this.mCallBackground = background;
        this.mCallForeground = foreground;
        return this;
    }

    public String getmSwitchCameraPressedBackground() {
        if (mSwitchCameraPressedBackground == null) {
            return mSwitchCameraBackground;
        }
        return mSwitchCameraPressedBackground;
    }

    /**
     * Sets the background color of the Switch Camera Button when the button is pressed.
     * @param background Hex code for the background color
     *
     *                   Example: config.setSwitchCameraPressedBackgroundColor("#00FFFF")
     */
    public UIConfig setSwitchCameraPressedBackgroundColor(String background) {
        this.mSwitchCameraPressedBackground = background;
        return this;
    }

    public String getmSwitchCameraPressedForeground() {
        if (mSwitchCameraPressedForeground == null) {
            return mSwitchCameraForeground;
        }
        return mSwitchCameraPressedForeground;
    }

    /**
     * Sets the foreground color of the Switch Camera Button when the button is pressed.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setSwitchCameraPressedForegroundColor("#FF0000")
     */
    public UIConfig setSwitchCameraPressedForegroundColor(String foreground) {
        this.mSwitchCameraPressedForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Switch Camera Button when the button is pressed.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setSwitchCameraPressedColor("#00FFFF", "#FF0000")
     */
    public UIConfig setSwitchCameraPressedColor(String background, String foreground) {
        this.mSwitchCameraPressedForeground = foreground;
        this.mSwitchCameraPressedBackground = background;
        return this;
    }

    public String getmAudioMutePressedBackground() {
        if (mAudioMutePressedBackground == null) {
            return mAudioMuteBackground;
        }
        return mAudioMutePressedBackground;
    }

    /**
     * Sets the background color of the Mute Audio Button when the button is pressed.
     * @param background Hex code for the background color
     *
     *                   Example: config.setAudioMutePressedBackgroundColor("#00FFFF")
     */
    public UIConfig setAudioMutePressedBackgroundColor(String background) {
        this.mAudioMutePressedBackground = background;
        return this;
    }

    public String getmAudioMutePressedForeground() {
        if (mAudioMutePressedForeground == null) {
            return mAudioMuteForeground;
        }
        return mAudioMutePressedForeground;
    }

    /**
     * Sets the foreground color of the Mute Audio Button when the button is pressed.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setAudioMutePressedForegroundColor("#FF0000")
     */
    public UIConfig setAudioMutePressedForegroundColor(String foreground) {
        this.mAudioMutePressedForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Mute Audio Button when the button is pressed.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setAudioMutePressedColor("#00FFFF", "#FF0000")
     */
    public UIConfig setAudioMutePressedColor(String background, String foreground) {
        this.mAudioMutePressedBackground = background;
        this.mAudioMutePressedForeground = foreground;
        return this;
    }

    public String getmVideoMutePressedBackground() {
        if (mVideoMutePressedBackground == null) {
            return mVideoMuteBackground;
        }
        return mVideoMutePressedBackground;
    }

    /**
     * Sets the background color of the Mute Video Button when the button is pressed.
     * @param background Hex code for the background color
     *
     *                   Example: config.setVideoMutePressedBackgroundColor("#FF0000")
     */
    public UIConfig setVideoMutePressedBackgroundColor(String background) {
        this.mVideoMutePressedBackground = background;
        return this;
    }

    public String getmVideoMutePressedForeground() {
        if (mVideoMutePressedForeground == null) {
            return mVideoMuteForeground;
        }
        return mVideoMutePressedForeground;
    }

    /**
     * Sets the foreground color of the Mute Video Button when the button is pressed.
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setVideoMutePressedForegroundColor("#FF0000")
     */
    public UIConfig setVideoMutePressedForegroundColor(String foreground) {
        this.mVideoMutePressedForeground = foreground;
        return this;
    }

    /**
     * Sets the background and foreground color of the Mute Video Button when the button is pressed.
     * @param background Hex code for the background color
     * @param foreground Hex code for the foreground color
     *
     *                   Example: config.setVideoMutePressedColor("#00FFFF", "#FF0000")
     */
    public UIConfig setVideoMutePressedColor(String background, String foreground) {
        this.mVideoMutePressedBackground = background;
        this.mVideoMutePressedForeground = foreground;
        return this;
    }
}
