package io.agora.agorauikit_android

import android.app.Activity
import android.content.Context
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger



/**
 * An interface for getting some common delegate callbacks without needing to subclass.
 */
interface AgoraVideoViewerDelegate {
    /**
     * Local user has joined a channel
     * @param channel Channel that the local user has joined.
     */
    fun joinedChannel(channel: String) {}

    /**
     * Local user has left a channel
     * @param channel Channel that the local user has left.
     */
    fun leftChannel(channel: String) {}

    /**
     * The token used to connect to the current active channel will expire in 30 seconds.
     * @param token Token that is currently used to connect to the channel.
     * @return Return true if the token fetch is being handled by this method.
     */
    fun tokenWillExpire(token: String?): Boolean {
        return false
    }

    /**
     * The token used to connect to the current active channel has expired.
     * @return Return true if the token fetch is being handled by this method.
     */
    fun tokenDidExpire(): Boolean {
        return false
    }
}

@ExperimentalUnsignedTypes
/**
 * View to contain all the video session objects, including camera feeds and buttons for settings
 */
open class AgoraVideoViewer : FrameLayout {

    /**
     * Style and organisation to be applied to all the videos in this view.
     */
    enum class Style {
        GRID, FLOATING, COLLECTION
    }

    /**
     * Gets and sets the role for the user. Either `.audience` or `.broadcaster`.
     */
    var userRole: Int = Constants.CLIENT_ROLE_BROADCASTER
        set(value: Int) {
            field = value
            this.agkit.setClientRole(value)
        }

    internal var controlContainer: ButtonContainer? = null
    internal var camButton: AgoraButton? = null
    internal var micButton: AgoraButton? = null
    internal var flipButton: AgoraButton? = null
    internal var endCallButton: AgoraButton? = null
    internal var screenShareButton: AgoraButton? = null

    companion object {}


    internal var remoteUserIDs: MutableSet<Int> = mutableSetOf()
    internal var userVideoLookup: MutableMap<Int, AgoraSingleVideoView> = mutableMapOf()
    internal val userVideosForGrid: Map<Int, AgoraSingleVideoView>
        get() {
            return if (this.style == Style.FLOATING) {
                this.userVideoLookup.filterKeys { it == this.overrideActiveSpeaker ?: this.activeSpeaker ?: this.userID }
            } else if (this.style == Style.GRID) {
                this.userVideoLookup
            } else {
                emptyMap()
            }
        }

    /**
     * Default beautification settings
     */
    open val beautyOptions: BeautyOptions
        get() {
            val beautyOptions = BeautyOptions()
            beautyOptions.smoothnessLevel = 1f
            beautyOptions.rednessLevel = 0.1f
            return beautyOptions
        }

    /**
     * Video views to be displayed in the floating collection view.
     */
    val collectionViewVideos: Map<Int, AgoraSingleVideoView>
        get() {
            return if (this.style == Style.FLOATING) {
                return this.userVideoLookup
            } else {
                emptyMap()
            }
        }

    /**
     * ID of the local user.
     * Setting to zero will tell Agora to assign one for you once connected.
     */
    public var userID: Int = 0
        internal set

    var isInRtcChannel: Boolean? = false

    /**
     * The most recently active speaker in the session.
     * This will only ever be set to remote users, not the local user.
     */
    public var activeSpeaker: Int? = null
        internal set
    private val newHandler = AgoraVideoViewerHandler(this)
    val agoraRtmClientHandler = AgoraRtmClientHandler(this)
    val agoraRtmChannelHandler = AgoraRtmChannelHandler(this)

    internal fun addUserVideo(userId: Int): AgoraSingleVideoView {
        this.userVideoLookup[userId]?.let { remoteView ->
            return remoteView
        }
        val remoteVideoView =
            AgoraSingleVideoView(this.context, userId, this.agoraSettings.colors.micFlag)
        remoteVideoView.canvas.renderMode = this.agoraSettings.videoRenderMode
        this.agkit.setupRemoteVideo(remoteVideoView.canvas)
//        this.agkit.setRemoteVideoRenderer(remoteVideoView.uid, remoteVideoView.textureView)
        this.userVideoLookup[userId] = remoteVideoView
        if (this.activeSpeaker == null) {
            this.activeSpeaker = userId
        }
        this.reorganiseVideos()
        return remoteVideoView
    }

    internal fun removeUserVideo(uid: Int, reogranise: Boolean = true) {
        val userSingleView = this.userVideoLookup[uid] ?: return
//        val canView = userSingleView.hostingView ?: return
        this.agkit.muteRemoteVideoStream(uid, true)
        userSingleView.canvas.view = null
        this.userVideoLookup.remove(uid)

        this.activeSpeaker.let {
            if (it == uid) this.setRandomSpeaker()
        }
        if (reogranise) {
            this.reorganiseVideos()
        }
    }

    internal fun setRandomSpeaker() {
        this.activeSpeaker = this.userVideoLookup.keys.shuffled().firstOrNull { it != this.userID }
    }

    /**
     * Active speaker override.
     */
    public var overrideActiveSpeaker: Int? = null
        set(newValue) {
            val oldValue = this.overrideActiveSpeaker
            field = newValue
            if (field != oldValue) {
                this.reorganiseVideos()
            }
        }

    internal fun addLocalVideo(): AgoraSingleVideoView? {
        if (this.userID == 0 || this.userVideoLookup.containsKey(this.userID)) {
            return this.userVideoLookup[this.userID]
        }
        val vidView = AgoraSingleVideoView(this.context, 0, this.agoraSettings.colors.micFlag)
        vidView.canvas.renderMode = this.agoraSettings.videoRenderMode
        this.agkit.setupLocalVideo(vidView.canvas)
        this.userVideoLookup[this.userID] = vidView
        this.reorganiseVideos()
        return vidView
    }


    internal var connectionData: AgoraConnectionData

    /**
     * Creates an AgoraVideoViewer object, to be placed anywhere in your application.
     * @param context: Application context
     * @param connectionData: Storing struct for holding data about the connection to Agora service.
     * @param style: Style and organisation to be applied to all the videos in this AgoraVideoViewer.
     * @param agoraSettings: Settings for this viewer. This can include style customisations and information of where to get new tokens from.
     * @param delegate: Delegate for the AgoraVideoViewer, used for some important callback methods.
     */
    @Throws(Exception::class)
    public constructor(
        context: Context,
        connectionData: AgoraConnectionData,
        style: Style = Style.FLOATING,
        agoraSettings: AgoraSettings = AgoraSettings(),
        delegate: AgoraVideoViewerDelegate? = null,
    ) : super(context) {
        this.connectionData = connectionData
        this.style = style
        this.agoraSettings = agoraSettings
        this.delegate = delegate
//        this.setBackgroundColor(Color.BLUE)
        initAgoraEngine()
        this.addView(
            this.backgroundVideoHolder,
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
        )
        this.addView(
            this.floatingVideoHolder,
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 200)
        )
        this.floatingVideoHolder.setBackgroundColor(this.agoraSettings.colors.floatingBackgroundColor)
        this.floatingVideoHolder.background.alpha =
            this.agoraSettings.colors.floatingBackgroundAlpha
    }

    val agoraRtmController = AgoraRtmController(context, this)

    @Throws(Exception::class)
    private fun initAgoraEngine() {
        if (connectionData.appId == "my-app-id") {
            Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "Change the App ID!")
            throw IllegalArgumentException("Change the App ID!")
        }
        this.agkit = RtcEngine.create(context, connectionData.appId, this.newHandler)
        agkit.enableAudioVolumeIndication(1000, 3, true)
        agkit.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        agkit.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        agkit.enableVideo()
        agkit.setVideoEncoderConfiguration(VideoEncoderConfiguration())
        if (agoraSettings.enableRtm) {
            agoraRtmController.initAgoraRtm(context)
        }
    }

    /**
     * Delegate for the AgoraVideoViewer, used for some important callback methods.
     */
    public var delegate: AgoraVideoViewerDelegate? = null

    internal var floatingVideoHolder: RecyclerView = RecyclerView(context)
    internal var backgroundVideoHolder: RecyclerView = RecyclerView(context)

    /**
     * Settings and customisations such as position of on-screen buttons, collection view of all channel members,
     * as well as agora video configuration.
     */
    public var agoraSettings: AgoraSettings = AgoraSettings()
        internal set

    /**
     * Style and organisation to be applied to all the videos in this AgoraVideoViewer.
     */
    public var style: Style
        set(value: Style) {
            val oldValue = field
            field = value
            if (oldValue != value) {
//                this.backgroundVideoHolder.visibility = if (value == Style.COLLECTION) INVISIBLE else VISIBLE
                this.reorganiseVideos()
            }
        }

    /**
     * RtcEngine being used by this AgoraVideoViewer
     */
    public lateinit var agkit: RtcEngine
        internal set
    public lateinit var agRtmClient: RtmClient
        internal set
    lateinit var agRtmChannel: RtmChannel
        internal set

    fun isAgRtmChannelInitialized() = ::agRtmChannel.isInitialized

    fun isAgRrtmClientInitialized() = ::agRtmClient.isInitialized

    /// VideoControl

    internal fun setupAgoraVideo() {
        if (this.agkit.enableVideo() < 0) {
            Logger.getLogger("AgoraUIKit").log(Level.WARNING, "Could not enable video")
            return
        }
        if (this.controlContainer == null) {
            this.addVideoButtons()
        }
        this.agkit.setVideoEncoderConfiguration(this.agoraSettings.videoConfiguration)
    }

    /**
     * Leave channel stops all preview elements
     * @return Same return as RtcEngine.leaveChannel, 0 means no problem, less than 0 means there was an issue leaving
     */
    fun leaveChannel(): Int {
        val channelName = this.connectionData.channel
        if (channelName == null) {
            return 0
        }
        this.agkit.setupLocalVideo(null)
        if (this.userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            this.agkit.stopPreview()
        }
        this.activeSpeaker = null
        (this.context as Activity).runOnUiThread {
            this.remoteUserIDs.forEach { this.removeUserVideo(it, false) }
            this.remoteUserIDs = mutableSetOf()
            this.userVideoLookup = mutableMapOf()
            this.reorganiseVideos()
            this.controlContainer?.visibility = INVISIBLE
        }

        val leaveChannelRtn = this.agkit.leaveChannel()
        if (leaveChannelRtn >= 0) {
            this.connectionData.channel = null
            this.delegate?.leftChannel(channelName)
        }
        return leaveChannelRtn
    }

    /**
     * Join the Agora channel with optional token request
     * @param channel: Channel name to join
     * @param fetchToken: Whether the token should be fetched before joining the channel. A token will only be fetched if a token URL is provided in AgoraSettings.
     * @param role: [AgoraClientRole](https://docs.agora.io/en/Video/API%20Reference/oc/Constants/AgoraClientRole.html) to join the channel as. Default: `.broadcaster`
     * @param uid: UID to be set when user joins the channel, default will be 0.
     */
    fun join(channel: String, fetchToken: Boolean, role: Int? = null, uid: Int? = null) {
        this.setupAgoraVideo()
        getRtcToken(channel, role, uid, fetchToken)

        if (agoraSettings.enableRtm){
            getRtmToken(fetchToken)
        }
    }

    private fun getRtcToken(channel: String, role: Int? = null, uid: Int? = null, fetchToken: Boolean) {
        if (fetchToken) {
            this.agoraSettings.tokenURL?.let { tokenURL ->
                AgoraVideoViewer.Companion.fetchToken(
                    tokenURL, channel, uid ?: this.userID,
                    object : TokenCallback {
                        override fun onSuccess(token: String) {
                            this@AgoraVideoViewer.connectionData.appToken = token
                            this@AgoraVideoViewer.join(channel, token, role, uid)
                        }

                        override fun onError(error: TokenError) {
                            Logger.getLogger("AgoraUIKit", "Could not get RTC token: ${error.name}")
                        }
                    }
                )
            }
            return
        }
        this.join(channel, this.connectionData.appToken, role, uid)
    }

    private fun getRtmToken(fetchToken: Boolean) {
        if (connectionData.rtmId.isNullOrEmpty()) {
            agoraRtmController.generateRtmId()
        }

        if (fetchToken){
            this.agoraSettings.tokenURL?.let { tokenURL ->
                AgoraRtmController.Companion.fetchToken(
                    tokenURL,
                    rtmId = connectionData.rtmId as String,
                    completion = object : RtmTokenCallback {
                        override fun onSuccess(token: String) {
                            connectionData.rtmToken = token
                        }

                        override fun onError(error: RtmTokenError) {
                            Logger.getLogger("AgoraUIKit", "Could not get RTM token: ${error.name}")
                        }
                    })
            }
            return
        }
    }

    fun triggerLoginToRtm() {
        if (agoraSettings.enableRtm && isAgRrtmClientInitialized()) {
            agoraRtmController.loginToRtm()
        } else {
            Logger.getLogger("AgoraUIKit")
                .log(Level.WARNING, "Username is null or RTM client has not been initialized")
        }
    }

    /**
     * Join the Agora channel with optional token request
     * @param channel: Channel name to join
     * @param token: token to be applied to the channel join. Leave null to use an existing token or no token.
     * @param role: [AgoraClientRole](https://docs.agora.io/en/Video/API%20Reference/oc/Constants/AgoraClientRole.html) to join the channel as.
     * @param uid: UID to be set when user joins the channel, default will be 0.
     */
    fun join(channel: String, token: String? = null, role: Int? = null, uid: Int? = null) {

        if (role == Constants.CLIENT_ROLE_BROADCASTER) {
            AgoraVideoViewer.requestPermissions(this.context)
        }
        if (this.connectionData.channel != null) {
            if (this.connectionData.channel == channel) {
                // already in this channel
                return
            }
            val leaveChannelRtn = this.leaveChannel()
            if (leaveChannelRtn < 0) {
                // could not leave channel
                Logger.getLogger("AgoraUIKit")
                    .log(Level.WARNING, "Could not leave channel: $leaveChannelRtn")
            } else {
                this.join(channel, token, role, uid)
            }
            return
        }
        role?.let {
            if (it != this.userRole) {
                this.userRole = it
            }
        }
        uid?.let {
            this.userID = it
        }

        this.setupAgoraVideo()
        this.agkit.joinChannel(token, channel, null, this.userID)
    }

}
