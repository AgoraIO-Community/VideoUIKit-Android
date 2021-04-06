package io.agora.agorauikit_android

import android.graphics.Color
import io.agora.rtc.Constants
import io.agora.rtc.video.VideoEncoderConfiguration

class AgoraSettings {
    /** URL to fetch tokens from. If supplied, this package will automatically fetch tokens
     * when the Agora Engine indicates it will be needed.
     * It will follow the URL pattern found in
     * [AgoraIO-Community/agora-token-service](https://github.com/AgoraIO-Community/agora-token-service)
     */
    public var tokenURL: String? = null

    /**
     * Position, top, left, bottom or right.
     */
    public enum class Position {
        /// At the top of the view
        TOP,
        /// At the right of the view
        RIGHT,
        /// At the bottom of the view
        BOTTOM,
        /// At the left of the view
        LEFT
    }
    public enum class BuiltinButton {
        CAMERA,
        MIC,
        FLIP
    }
    /**
     * The rendering mode of the video view for all videos within the view.
     */
    public var videoRenderMode = Constants.RENDER_MODE_FIT
    /**
     * Where the buttons such as camera enable/disable should be positioned within the view.
     */
    public var buttonPosition = Position.BOTTOM
    /**
     * Where the floating collection view of video members be positioned within the view.
     */
    public var floatPosition = Position.TOP
    /**
     * Agora's video encoder configuration.
     */
    public var videoConfiguration: VideoEncoderConfiguration = VideoEncoderConfiguration()
    /**
     * Which buttons should be enabled in this AgoraVideoView.
     */
    public var enabledButtons: MutableSet<BuiltinButton> = mutableSetOf(BuiltinButton.CAMERA, BuiltinButton.MIC, BuiltinButton.FLIP)

    /**
     * Colors for views inside AgoraVideoViewer
     */
    public var colors = AgoraViewerColors()
    /**
     * Full string for low bitrate stream parameter, including key of `che.video.lowBitRateStreamParameter`.
     */
    public var lowBitRateStream: String? = null
    /**
     * Maximum number of videos in the grid view before the low bitrate is adopted.
     */
    public var gridThresholdHighBitrate = 5

    /**
     * Whether we are using dual stream mode, which helps to reduce Agora costs.
     */
    public var usingDualStream: Boolean
        get() = this.lowBitRateStream != null
        set(newValue) {
            if (newValue && this.lowBitRateStream != null) {
                return
            }
            if (newValue) {
                this.lowBitRateStream = defaultLowBitrateParam
            } else {
                this.lowBitRateStream = null
            }
        }
    public var extraButtons: MutableList<AgoraButton> = mutableListOf()
    companion object {
        private const val defaultLowBitrateParam = "{\"che.video.lowBitRateStreamParameter\":{\"width\":320,\"height\":180,\"frameRate\":5,\"bitRate\":140}}"
    }
}

class AgoraViewerColors {
    /**
     * Color of the view that signals a user has their mic muted. Default `Color.BLUE`
     */
    var micFlag: Int = Color.BLUE
    var floatingBackgroundColor: Int = Color.LTGRAY
    var floatingBackgroundAlpha: Int = 100
    var buttonBackgroundColor: Int = Color.LTGRAY
    var buttonBackgroundAlpha: Int = 255 / 5
}