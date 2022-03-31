package io.agora.agorauikit_android

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.BeautyOptions
import io.agora.rtc.video.VideoEncoderConfiguration
import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger
import io.agora.rtm.SendMessageOptions

import io.agora.rtm.RtmChannelMember

import io.agora.rtm.RtmFileMessage

import io.agora.rtm.RtmImageMessage

import io.agora.rtm.RtmMessage

import io.agora.rtm.RtmChannelAttribute

import io.agora.rtm.RtmChannelListener
import org.json.JSONObject


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
    private var generatedRtmId: String? = null
    private var isLoggedIn: Boolean? = false
    private var isInRtmChannel: Boolean? = false
    private var isInRtcChannel: Boolean? = false

    /**
     * The most recently active speaker in the session.
     * This will only ever be set to remote users, not the local user.
     */
    public var activeSpeaker: Int? = null
        internal set
    private val newHandler = AgoraVideoViewerHandler(this)

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
        if (connectionData.username != null) {
            initAgoraRtm(context)
        }
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
    }

    private fun initAgoraRtm(context: Context) {
        try {
            this.agRtmClient = RtmClient.createInstance(context, connectionData.appId,
                object : RtmClientListener {
                    override fun onConnectionStateChanged(state: Int, reason: Int) {
                        println("RTM Connection State Changed. state: $state, reason: $reason")
                    }

                    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
                        println("Peer message received from ${peerId.toString()}, " + rtmMessage.text.toString())
                        messageReceived(rtmMessage.text)
                    }

                    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
                        TODO("Not yet implemented")
                    }

                    override fun onMediaUploadingProgress(
                        p0: RtmMediaOperationProgress?,
                        p1: Long
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onMediaDownloadingProgress(
                        p0: RtmMediaOperationProgress?,
                        p1: Long
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onTokenExpired() {
                        TODO("Not yet implemented")
                    }

                    override fun onPeersOnlineStatusChanged(peerStatus: MutableMap<String, Int>?) {
                        println("onPeerOnlineStatusChanged: $peerStatus")
                    }
                })
        } catch (e: Exception) {
            Logger.getLogger("AgoraUIKit")
                .log(Level.SEVERE, "Failed to initialize Agora RTM SDK. Error: $e")
        }
        if (isLoggedIn == false) {
            loginToRtm()
        }
    }

    private fun loginToRtm() {
        if (connectionData.rtmId == null) {
            generateRtmId()
        }
        this.agRtmClient.login(
            connectionData.rtmToken,
            connectionData.rtmId?.let { connectionData.rtmId } ?: let { generatedRtmId },
            object : ResultCallback<Void?> {
                override fun onSuccess(responseInfo: Void?) {
                    isLoggedIn = true
                    Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "User logged in successfully")
                }

                override fun onFailure(errorInfo: ErrorInfo) {
                    Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "User login failed")
                }
            })
    }

    fun createRtmChannel() {
        try {

            var channelName: String =
                connectionData.rtmChannelName?.let { connectionData.rtmChannelName.toString() }
                    ?: let { connectionData.channel.toString() }
            this.agRtmChannel =
                agRtmClient.createChannel(channelName, object : RtmChannelListener {
                    override fun onMemberCountUpdated(memberCount: Int) {
                        println("Member count updated. Count: $memberCount")
                    }

                    override fun onAttributesUpdated(attributeList: MutableList<RtmChannelAttribute>?) {
                        println("onAttributesUpdated: $attributeList")
                    }

                    override fun onMessageReceived(
                        rtmMessage: RtmMessage,
                        rtmChannelMember: RtmChannelMember
                    ) {
                        println("Message: ${rtmMessage.text} from ${rtmChannelMember.channelId}")
                        messageReceived(rtmMessage.text)
                    }

                    override fun onImageMessageReceived(
                        p0: RtmImageMessage?,
                        p1: RtmChannelMember?
                    ) {
                        TODO("Not yet implemented")
                    }

                    override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
                        TODO("Not yet implemented")
                    }

                    override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
                        println("RTM Member joined: ${rtmChannelMember.userId}")
                        sendUserData(toChannel = false, peerRtmId = rtmChannelMember.userId)
                    }

                    override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
                        println("Member left: ${rtmChannelMember.userId}")
                    }
                })
        } catch (e: RuntimeException) {
            println("Failed to create RTM channel. Error: $e")
        }
        if (this@AgoraVideoViewer::agRtmChannel.isInitialized) {
            joinRtmChannel()
        }
    }

    private fun joinRtmChannel() {
        this.agRtmChannel.join(object : ResultCallback<Void> {
            override fun onSuccess(responseInfo: Void?) {
                isInRtmChannel = true
                Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM Channel Joined Successfully")
                if (isInRtmChannel == true) {
                    sendUserData(toChannel = true)
                }
            }

            override fun onFailure(errorInfo: ErrorInfo) {
                isInRtmChannel = false
                Logger.getLogger("AgoraUIKit")
                    .log(Level.SEVERE, "Failed to join RTM Channel. Error: $errorInfo")
            }
        })
    }

    private fun generateRtmId() {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        generatedRtmId = (1..10)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("");
    }

    private fun sendUserData(toChannel: Boolean, peerRtmId: String? = null) {

        val rtmId: String =
            (connectionData.rtmId?.let { connectionData.rtmId } ?: let { generatedRtmId }) as String

        var userData = """{
      "messageType": "UserData",
      "rtmId": "$rtmId",
      "rtcId": $userID,
      "username": "${connectionData.username}",
      "role": "$userRole",
      "agora": {
        "rtm": ${RtmClient.getSdkVersion()},
        "rtc": ${RtcEngine.getSdkVersion()}
      },
      "uikit": {
        "platform": "Android",
        "framework": "Kotlin",
        "version": "1.0.0"
      }
    }"""
        var message: RtmMessage = agRtmClient.createMessage()
        message.text = userData


        val option = SendMessageOptions()
        option.enableOfflineMessaging = true

        if (!toChannel) {
            agRtmClient.sendMessageToPeer(
                peerRtmId,
                message,
                option,
                object : ResultCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        println("UserData message sent to $peerRtmId")
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        println("Failed to send UserData to peer: $peerRtmId")
                    }
                })
        } else {
            agRtmChannel.sendMessage(message, option, object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    println("USerData message sent to channel")
                }

                override fun onFailure(p0: ErrorInfo?) {
                    println("Failed to send UserData to channel")
                }
            })
        }

    }

    internal fun askForUserMic(peerRtcId: Int, isMicEnabled: Boolean) {
        var peerRtmId: String? = null
        var json: String = """{
        "messageType": "MuteRequest",
        "rtcId": $peerRtcId,
        "mute": $isMicEnabled,
        "device": "0",
        "isForceFul": "false"
    }"""

        var message: RtmMessage = agRtmClient.createMessage()
        message.text = json

        val option = SendMessageOptions()
        option.enableOfflineMessaging = true

        if (peerRtcId == userID) {
            println("Can't send message to the local user")
        } else {
            if (this.agoraSettings.uidToUserIdMap.containsKey(peerRtcId)) {
                peerRtmId = this.agoraSettings.uidToUserIdMap.getValue(peerRtcId)

                agRtmClient.sendMessageToPeer(
                    peerRtmId,
                    message,
                    option,
                    object : ResultCallback<Void> {
                        override fun onSuccess(p0: Void?) {
                            println("Mic Request message sent to $peerRtmId")
                        }
                        override fun onFailure(p0: ErrorInfo?) {
                            println("Failed to send mic request message to $peerRtmId. Error : $p0")
                        }
                    })
            }
        }
    }

    internal fun askForUserCamera(peerRtcId: Int, isCameraEnabled: Boolean) {
        var peerRtmId: String? = null
        var json: String = """{
        "messageType": "CameraRequest",
        "rtcId": $peerRtcId,
        "mute": $isCameraEnabled,
        "device": "1",
        "isForceFul": "false"
    }"""

        var message: RtmMessage = agRtmClient.createMessage()
        message.text = json

        val option = SendMessageOptions()
        option.enableOfflineMessaging = true

        if (peerRtcId == userID) {
            println("Can't send message to the local user")
        } else {
            if (this.agoraSettings.uidToUserIdMap.containsKey(peerRtcId)) {
                peerRtmId = this.agoraSettings.uidToUserIdMap.getValue(peerRtcId)

                agRtmClient.sendMessageToPeer(
                    peerRtmId,
                    message,
                    option,
                    object : ResultCallback<Void> {
                        override fun onSuccess(p0: Void?) {
                            println("Camera Request message sent to $peerRtmId")
                        }

                        override fun onFailure(p0: ErrorInfo?) {
                            println("Failed to send cmessage $peerRtmId. Error : $p0")
                        }
                    })
            }
        }
    }

    private fun addToUidToUserIdMap(rtcId: Int, rtmId: String) {
        this.agoraSettings.uidToUserIdMap.putIfAbsent(rtcId, rtmId)
    }

    private fun addToUserRtmMap(rtmId: String, message: String) {
        this.agoraSettings.userRtmMap.putIfAbsent(rtmId, message)
    }

    private fun messageReceived(message: String) {
        val messageMap = JSONObject(message)
        when (messageMap.getString("messageType")) {
            "UserData" -> {
                val rtcId = messageMap.getInt("rtcId")
                val rtmId = messageMap.getString("rtmId")
                addToUidToUserIdMap(rtcId = rtcId.toInt(), rtmId = rtmId)
                addToUserRtmMap(rtmId = rtmId, message = message)
            }
            "MuteRequest" -> {
                var micStatus = messageMap.getBoolean("mute")
                val snackbar = Snackbar.make(
                    this,
                    "Please " + if (micStatus) "unmute" else "mute" + " your mic",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction(if (micStatus) "unmute" else "mute") {
                    agkit.muteLocalAudioStream(!micStatus);
                    micStatus = !micStatus
                    micButton?.background?.setTint(if (micStatus) Color.RED else Color.GRAY)
                    micButton?.isSelected = micStatus
                    this.userVideoLookup[this.userID]?.mutedFlag?.visibility =
                        if (micStatus) VISIBLE else INVISIBLE
                    this.userVideoLookup[this.userID]?.audioMuted = micStatus
                }
                snackbar.show()
            }
            "CameraRequest" -> {
                var camStatus = messageMap.getBoolean("mute")
                val snackbar = Snackbar.make(
                    this,
                    "Please " + if (camStatus) "enable" else "disable" + " your camera",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction(if (camStatus) "enable" else "disable") {
                    agkit.enableLocalVideo(camStatus)
                    camStatus = !camStatus
                    camButton?.background?.setTint(if (camStatus) Color.RED else Color.GRAY)
                    camButton?.isSelected = camStatus
                    this.userVideoLookup[this.userID]?.backgroundView?.visibility =
                        if (camStatus) VISIBLE else INVISIBLE
                    this.userVideoLookup[this.userID]?.videoMuted = !camStatus
                }
                snackbar.show()
            }
        }
    }
//    constructor(context: Context) : super(context)
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
    public lateinit var agRtmChannel: RtmChannel
        internal set
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
                            Logger.getLogger("AgoraUIKit", "Could not get token: ${error.name}")
                        }
                    }
                )
            }
            return
        }
        this.join(channel, this.connectionData.appToken, role, uid)
        isInRtcChannel = true
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
