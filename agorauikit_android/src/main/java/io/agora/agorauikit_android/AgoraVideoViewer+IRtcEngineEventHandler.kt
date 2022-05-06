package io.agora.agorauikit_android

import android.app.Activity
import android.graphics.Rect
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.models.UserInfo
import java.util.logging.Level
import java.util.logging.Logger


@ExperimentalUnsignedTypes
class AgoraVideoViewerHandler(private val hostView: AgoraVideoViewer) :
    IRtcEngineEventHandler() {

    override fun onClientRoleChanged(oldRole: Int, newRole: Int) {
        super.onClientRoleChanged(oldRole, newRole)
        val isHost = newRole == Constants.CLIENT_ROLE_BROADCASTER
        if (!isHost) {
            this.hostView.userVideoLookup.remove(this.hostView.userID)
        } else if (!this.hostView.userVideoLookup.contains(this.hostView.userID)) {
            (this.hostView.context as Activity).runOnUiThread(Runnable {
                this.hostView.addLocalVideo()
            })
        }
        // Only show the camera options when we are a broadcaster
//            this.getControlContainer().isHidden = !isHost

        this.hostView.agoraSettings.agoraRtcEventHandler?.onClientRoleChanged(oldRole, newRole)
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "onUserJoined: $uid")
        super.onUserJoined(uid, elapsed)
        this.hostView.remoteUserIDs.add(uid)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserJoined(uid, elapsed)
    }

    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        super.onRemoteAudioStateChanged(uid, state, reason, elapsed)
        Logger.getLogger("AgoraUIKit").log(Level.WARNING, "setting muted state: " + state)
        (this.hostView.context as Activity).runOnUiThread {
            if (state == Constants.REMOTE_AUDIO_STATE_STOPPED || state == Constants.REMOTE_AUDIO_STATE_STARTING || state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                if (state == Constants.REMOTE_AUDIO_STATE_STARTING && !this.hostView.userVideoLookup.containsKey(
                        uid
                    )
                ) {
                    this.hostView.addUserVideo(uid)
                }
                if (this.hostView.userVideoLookup.containsKey(uid)) {
                    this.hostView.userVideoLookup[uid]?.audioMuted =
                        state == Constants.REMOTE_AUDIO_STATE_STOPPED
                }
            }
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRemoteAudioStateChanged(uid, state, reason, elapsed)
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        super.onUserOffline(uid, reason)
        Logger.getLogger("AgoraUIKit").log(Level.WARNING, "User offline: $reason")
        if (reason == Constants.USER_OFFLINE_QUIT || reason == Constants.USER_OFFLINE_DROPPED) {
            this.hostView.remoteUserIDs.remove(uid)
        }
        if (this.hostView.userVideoLookup.containsKey(uid)) {
            (this.hostView.context as Activity).runOnUiThread {
                this.hostView.removeUserVideo(uid)
            }
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserOffline(uid, reason)
    }

    override fun onActiveSpeaker(uid: Int) {
        super.onActiveSpeaker(uid)
        this.hostView.activeSpeaker = uid

        this.hostView.agoraSettings.agoraRtcEventHandler?.onActiveSpeaker(uid)
    }

    override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
        (this.hostView.context as Activity).runOnUiThread {
            when (state) {
                Constants.REMOTE_VIDEO_STATE_DECODING -> {
                    if (!this.hostView.userVideoLookup.containsKey(uid)) {
                        this.hostView.addUserVideo(uid)
                    }
                    this.hostView.userVideoLookup[uid]?.videoMuted = false
                    if (this.hostView.activeSpeaker == null && uid != this.hostView.userID) {
                        this.hostView.activeSpeaker = uid
                    }
                }
                Constants.REMOTE_VIDEO_STATE_STOPPED -> {
                    this.hostView.userVideoLookup[uid]?.videoMuted = true
                }
            }
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRemoteVideoStateChanged(uid, state, reason, elapsed)
    }

    override fun onLocalVideoStateChanged(localVideoState: Int, error: Int) {
        super.onLocalVideoStateChanged(localVideoState, error)
        (this.hostView.context as Activity).runOnUiThread {
            when (localVideoState) {
                Constants.LOCAL_VIDEO_STREAM_STATE_CAPTURING, Constants.LOCAL_VIDEO_STREAM_STATE_STOPPED -> {
                    this.hostView.userVideoLookup[
                            this.hostView.userID
                    ]?.videoMuted = localVideoState == Constants.LOCAL_VIDEO_STREAM_STATE_STOPPED
                }
            }
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalVideoStateChanged(localVideoState, error)
    }

    override fun onLocalAudioStateChanged(state: Int, error: Int) {
        super.onLocalAudioStateChanged(state, error)
        (this.hostView.context as Activity).runOnUiThread {
            when (state) {
                Constants.LOCAL_AUDIO_STREAM_STATE_CAPTURING, Constants.LOCAL_AUDIO_STREAM_STATE_STOPPED -> {
                    this.hostView.userVideoLookup[
                            this.hostView.userID
                    ]?.audioMuted = state == Constants.LOCAL_AUDIO_STREAM_STATE_STOPPED
                }
            }
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalAudioStateChanged(state, error)
    }

    override fun onFirstLocalAudioFramePublished(elapsed: Int) {
        super.onFirstLocalAudioFramePublished(elapsed)
//        this.hostView.addLocalVideo()?.audioMuted = false

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFirstLocalAudioFramePublished(elapsed)
    }

    override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
        super.onJoinChannelSuccess(channel, uid, elapsed)

        this.hostView.connectionData.channel = channel
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "join channel success")
        this.hostView.userID = uid
        if (this.hostView.userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            (this.hostView.context as Activity).runOnUiThread(Runnable {
                this.hostView.addLocalVideo()
            })
        }
        channel?.let {
            this.hostView.delegate?.joinedChannel(it)
        }
        this.hostView.isInRtcChannel = true
        if (this.hostView.agoraRtmController.loginStatus != AgoraRtmController.LoginStatus.LOGGED_IN) {
            this.hostView.triggerLoginToRtm()
        }

        this.hostView.agoraSettings.agoraRtcEventHandler?.onJoinChannelSuccess(channel,uid, elapsed)
    }

    override fun onTokenPrivilegeWillExpire(token: String?) {
        super.onTokenPrivilegeWillExpire(token)
        if (this.hostView.delegate?.tokenWillExpire(token) == true) {
            return
        }
        this.hostView.fetchRenewToken()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onTokenPrivilegeWillExpire(token)
    }

    override fun onRequestToken() {
        super.onRequestToken()
        if (this.hostView.delegate?.tokenDidExpire() == true) {
            return
        }
        this.hostView.fetchRenewToken()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRequestToken()
    }

    override fun onApiCallExecuted(error: Int, api: String?, result: String?) {
        super.onApiCallExecuted(error, api, result)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onApiCallExecuted(error, api, result)
    }

    override fun onAudioEffectFinished(soundId: Int) {
        super.onAudioEffectFinished(soundId)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioEffectFinished(soundId)
    }

    override fun onAudioMixingStateChanged(state: Int, reason: Int) {
        super.onAudioMixingStateChanged(state, reason)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioMixingStateChanged(state, reason)
    }

    override fun onAudioPublishStateChanged(
        channel: String?,
        oldState: Int,
        newState: Int,
        elapseSinceLastState: Int
    ) {
        super.onAudioPublishStateChanged(channel, oldState, newState, elapseSinceLastState)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioPublishStateChanged(channel, oldState, newState, elapseSinceLastState)
    }

    override fun onAudioRouteChanged(routing: Int) {
        super.onAudioRouteChanged(routing)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioRouteChanged(routing)
    }

    override fun onAudioSubscribeStateChanged(
        channel: String?,
        uid: Int,
        oldState: Int,
        newState: Int,
        elapseSinceLastState: Int
    ) {
        super.onAudioSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)
    }

    override fun onAudioVolumeIndication(speakers: Array<out AudioVolumeInfo>?, totalVolume: Int) {
        super.onAudioVolumeIndication(speakers, totalVolume)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onAudioVolumeIndication(speakers, totalVolume)
    }

    override fun onCameraExposureAreaChanged(rect: Rect?) {
        super.onCameraExposureAreaChanged(rect)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onCameraExposureAreaChanged(rect)
    }

    override fun onCameraFocusAreaChanged(rect: Rect?) {
        super.onCameraFocusAreaChanged(rect)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onCameraExposureAreaChanged(rect)
    }

    override fun onChannelMediaRelayEvent(code: Int) {
        super.onChannelMediaRelayEvent(code)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onChannelMediaRelayEvent(code)
    }

    override fun onChannelMediaRelayStateChanged(state: Int, code: Int) {
        super.onChannelMediaRelayStateChanged(state, code)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onChannelMediaRelayStateChanged(state, code)
    }

    override fun onClientRoleChangeFailed(reason: Int, currentRole: Int) {
        super.onClientRoleChangeFailed(reason, currentRole)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onClientRoleChangeFailed(reason, currentRole)
    }

    override fun onConnectionLost() {
        super.onConnectionLost()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onConnectionLost()
    }

    override fun onConnectionStateChanged(state: Int, reason: Int) {
        super.onConnectionStateChanged(state, reason)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onConnectionStateChanged(state, reason)
    }

    override fun onContentInspectResult(result: Int) {
        super.onContentInspectResult(result)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onContentInspectResult(result)
    }

    override fun onError(err: Int) {
        super.onError(err)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onError(err)
    }

    override fun onFacePositionChanged(
        imageWidth: Int,
        imageHeight: Int,
        faces: Array<out AgoraFacePositionInfo>?
    ) {
        super.onFacePositionChanged(imageWidth, imageHeight, faces)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFacePositionChanged(imageHeight, imageHeight, faces)
    }

    override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
        super.onFirstLocalVideoFrame(width, height, elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFirstLocalVideoFrame(width, height, elapsed)
    }

    override fun onFirstLocalVideoFramePublished(elapsed: Int) {
        super.onFirstLocalVideoFramePublished(elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFirstLocalAudioFramePublished(elapsed)
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        super.onFirstRemoteVideoDecoded(uid, width, height, elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFirstRemoteVideoDecoded(uid, width, height, elapsed)
    }

    override fun onFirstRemoteVideoFrame(uid: Int, width: Int, height: Int, elapsed: Int) {
        super.onFirstRemoteVideoFrame(uid, width, height, elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onFirstRemoteVideoFrame(uid, width, height, elapsed)
    }

    override fun onLastmileProbeResult(result: LastmileProbeResult?) {
        super.onLastmileProbeResult(result)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLastmileProbeResult(result)
    }

    override fun onLastmileQuality(quality: Int) {
        super.onLastmileQuality(quality)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLastmileQuality(quality)
    }

    override fun onLeaveChannel(stats: RtcStats?) {
        super.onLeaveChannel(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLeaveChannel(stats)
    }

    override fun onLocalAudioStats(stats: LocalAudioStats?) {
        super.onLocalAudioStats(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalAudioStats(stats)
    }

    override fun onLocalPublishFallbackToAudioOnly(isFallbackOrRecover: Boolean) {
        super.onLocalPublishFallbackToAudioOnly(isFallbackOrRecover)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalPublishFallbackToAudioOnly(isFallbackOrRecover)
    }

    override fun onLocalUserRegistered(uid: Int, userAccount: String?) {
        super.onLocalUserRegistered(uid, userAccount)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalUserRegistered(uid, userAccount)
    }

    override fun onLocalVideoStats(stats: LocalVideoStats?) {
        super.onLocalVideoStats(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalVideoStats(stats)
    }

    override fun onLocalVoicePitchInHz(pitchInHz: Int) {
        super.onLocalVoicePitchInHz(pitchInHz)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onLocalVoicePitchInHz(pitchInHz)
    }

    override fun onMediaEngineLoadSuccess() {
        super.onMediaEngineLoadSuccess()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onMediaEngineLoadSuccess()
    }

    override fun onMediaEngineStartCallSuccess() {
        super.onMediaEngineStartCallSuccess()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onMediaEngineStartCallSuccess()
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        super.onNetworkQuality(uid, txQuality, rxQuality)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onNetworkQuality(uid, txQuality, rxQuality)
    }

    override fun onNetworkTypeChanged(type: Int) {
        super.onNetworkTypeChanged(type)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onNetworkTypeChanged(type)
    }

    override fun onProxyConnected(
        channel: String?,
        uid: Int,
        proxyType: Int,
        localProxyIp: String?,
        elapsed: Int
    ) {
        super.onProxyConnected(channel, uid, proxyType, localProxyIp, elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onProxyConnected(channel, uid, proxyType, localProxyIp, elapsed)
    }

    override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onRejoinChannelSuccess(channel, uid, elapsed)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRejoinChannelSuccess(channel, uid, elapsed)
    }

    override fun onRemoteAudioStats(stats: RemoteAudioStats?) {
        super.onRemoteAudioStats(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRemoteAudioStats(stats)
    }

    override fun onRemoteSubscribeFallbackToAudioOnly(uid: Int, isFallbackOrRecover: Boolean) {
        super.onRemoteSubscribeFallbackToAudioOnly(uid, isFallbackOrRecover)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRemoteSubscribeFallbackToAudioOnly(uid, isFallbackOrRecover)
    }

    override fun onRemoteVideoStats(stats: RemoteVideoStats?) {
        super.onRemoteVideoStats(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRemoteVideoStats(stats)
    }

    override fun onRequestAudioFileInfo(info: AudioFileInfo?, error: Int) {
        super.onRequestAudioFileInfo(info, error)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRequestAudioFileInfo(info, error)
    }

    override fun onRtcStats(stats: RtcStats?) {
        super.onRtcStats(stats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRtcStats(stats)
    }

    override fun onRtmpStreamingEvent(url: String?, error: Int) {
        super.onRtmpStreamingEvent(url, error)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRtmpStreamingEvent(url, error)
    }

    override fun onRtmpStreamingStateChanged(url: String?, state: Int, errCode: Int) {
        super.onRtmpStreamingStateChanged(url, state, errCode)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onRtmpStreamingStateChanged(url, state, errCode)
    }

    override fun onSnapshotTaken(
        channel: String?,
        uid: Int,
        filePath: String?,
        width: Int,
        height: Int,
        errCode: Int
    ) {
        super.onSnapshotTaken(channel, uid, filePath, width, height, errCode)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onSnapshotTaken(channel, uid, filePath, width, height, errCode)
    }

    override fun onStreamInjectedStatus(url: String?, uid: Int, status: Int) {
        super.onStreamInjectedStatus(url, uid, status)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onStreamInjectedStatus(url, uid, status)
    }

    override fun onStreamMessage(uid: Int, streamId: Int, data: ByteArray?) {
        super.onStreamMessage(uid, streamId, data)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onStreamMessage(uid, streamId, data)
    }

    override fun onStreamMessageError(
        uid: Int,
        streamId: Int,
        error: Int,
        missed: Int,
        cached: Int
    ) {
        super.onStreamMessageError(uid, streamId, error, missed, cached)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onStreamMessageError(uid, streamId, error, missed, cached)
    }

    override fun onTranscodingUpdated() {
        super.onTranscodingUpdated()

        this.hostView.agoraSettings.agoraRtcEventHandler?.onTranscodingUpdated()
    }

    override fun onUploadLogResult(requestId: String?, success: Boolean, reason: Int) {
        super.onUploadLogResult(requestId, success, reason)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUploadLogResult(requestId, success, reason)
    }

    override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
        super.onUserEnableLocalVideo(uid, enabled)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserEnableLocalVideo(uid, enabled)
    }

    override fun onUserEnableVideo(uid: Int, enabled: Boolean) {
        super.onUserEnableVideo(uid, enabled)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserEnableVideo(uid, enabled)
    }

    override fun onUserInfoUpdated(uid: Int, userInfo: UserInfo?) {
        super.onUserInfoUpdated(uid, userInfo)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserInfoUpdated(uid, userInfo)
    }

    override fun onUserMuteAudio(uid: Int, muted: Boolean) {
        super.onUserMuteAudio(uid, muted)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserMuteAudio(uid, muted)
    }

    override fun onUserMuteVideo(uid: Int, muted: Boolean) {
        super.onUserMuteVideo(uid, muted)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserMuteVideo(uid, muted)
    }

    override fun onUserSuperResolutionEnabled(uid: Int, enabled: Boolean, reason: Int) {
        super.onUserSuperResolutionEnabled(uid, enabled, reason)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onUserSuperResolutionEnabled(uid, enabled, reason)
    }

    override fun onVideoPublishStateChanged(
        channel: String?,
        oldState: Int,
        newState: Int,
        elapseSinceLastState: Int
    ) {
        super.onVideoPublishStateChanged(channel, oldState, newState, elapseSinceLastState)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onVideoPublishStateChanged(channel, oldState, newState, elapseSinceLastState)
    }

    override fun onVideoSizeChanged(uid: Int, width: Int, height: Int, rotation: Int) {
        super.onVideoSizeChanged(uid, width, height, rotation)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onVideoSizeChanged(uid, width, height, rotation)
    }

    override fun onVideoSubscribeStateChanged(
        channel: String?,
        uid: Int,
        oldState: Int,
        newState: Int,
        elapseSinceLastState: Int
    ) {
        super.onVideoSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onVideoSubscribeStateChanged(channel, uid, oldState, newState, elapseSinceLastState)
    }

    override fun onVirtualBackgroundSourceEnabled(enabled: Boolean, reason: Int) {
        super.onVirtualBackgroundSourceEnabled(enabled, reason)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onVirtualBackgroundSourceEnabled(enabled, reason)
    }

    override fun onWarning(warn: Int) {
        super.onWarning(warn)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onWarning(warn)
    }

    override fun onWlAccMessage(reason: Int, action: Int, wlAccMsg: String?) {
        super.onWlAccMessage(reason, action, wlAccMsg)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onWlAccMessage(reason, action, wlAccMsg)
    }

    override fun onWlAccStats(currentStats: WlAccStats?, averageStats: WlAccStats?) {
        super.onWlAccStats(currentStats, averageStats)

        this.hostView.agoraSettings.agoraRtcEventHandler?.onWlAccStats(currentStats, averageStats)
    }
}
