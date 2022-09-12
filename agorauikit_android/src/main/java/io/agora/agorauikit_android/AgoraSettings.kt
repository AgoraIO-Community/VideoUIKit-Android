package io.agora.agorauikit_android

import android.graphics.Color
import io.agora.agorauikit_android.AgoraRtmController.UserData
import io.agora.rtc2.Constants
import io.agora.rtc2.video.VideoEncoderConfiguration

/**
 * Settings used for the display and behaviour of AgoraVideoViewer
 */
class AgoraSettings {

    /**
     * Maps user RTM ID to the user data
     */
    internal var userRtmMap = mutableMapOf<String, UserData>()

    /**
     * Maps RTC ID to RTM ID
     */
    internal var uidToUserIdMap = mutableMapOf<Int, String>()

    /**
     * Whether RTM should be initialised and used
     */
    public var rtmEnabled: Boolean = true
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
        /**
         *  At the top of the view
         */
        TOP,

        /**
         *  At the right of the view
         */
        RIGHT,

        /**
         *  At the bottom of the view
         */
        BOTTOM,

        /**
         *  At the left of the view
         */
        LEFT
    }

    /**
     * Enum value for all the default buttons offered by the VideoUIKit
     */
    public enum class BuiltinButton {
        CAMERA,
        MIC,
        FLIP,
        END
    }
    /**
     * The rendering mode of the video view for all videos within the view.
     */
    public var videoRenderMode = Constants.RENDER_MODE_FIT
    /**
     * Where the buttons such as camera enable/disable should be positioned within the view.
     * TODO: This is not yet implemented
     */
    public var buttonPosition = Position.BOTTOM
    /**
     * Where the floating collection view of video members be positioned within the view.
     * TODO: This is not yet implemented
     */
    public var floatPosition = Position.TOP
    /**
     * Agora's video encoder configuration.
     */
    public var videoConfiguration: VideoEncoderConfiguration = VideoEncoderConfiguration()
    /**
     * Which buttons should be enabled in this AgoraVideoView.
     */
    public var enabledButtons: MutableSet<BuiltinButton> = mutableSetOf(
        BuiltinButton.CAMERA, BuiltinButton.MIC, BuiltinButton.FLIP, BuiltinButton.END
    )

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

    /**
     * A mutable list to add buttons to the default list of [BuiltinButton]
     */
    public var extraButtons: MutableList<AgoraButton> = mutableListOf()
    companion object {
        private const val defaultLowBitrateParam = "{\"che.video.lowBitRateStreamParameter\":{\"width\":320,\"height\":180,\"frameRate\":5,\"bitRate\":140}}"
    }
}

/**
 * Colors for various views inside AgoraVideoViewer
 */
class AgoraViewerColors {
    /**
     * Color of the view that signals a user has their mic muted. Default `Color.BLUE`
     */
    var micFlag: Int = Color.BLUE
    /**
     * Background colour of the scrollable floating viewer
     */
    var floatingBackgroundColor: Int = Color.LTGRAY
    /**
     * Opacity of the floating viewer background (0-255)
     */
    var floatingBackgroundAlpha: Int = 100
    /**
     * Background colour of the button holder
     */
    var buttonBackgroundColor: Int = Color.LTGRAY
    /**
     * Opacity of the button holder background (0-255)
     */
    var buttonBackgroundAlpha: Int = 255 / 5
}
