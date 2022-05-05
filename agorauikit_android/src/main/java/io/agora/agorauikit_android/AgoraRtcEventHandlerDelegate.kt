package io.agora.agorauikit_android

import android.graphics.Rect
import io.agora.rtc.IRtcEngineEventHandler

import io.agora.rtc.models.UserInfo


open class AgoraRtcEventHandlerDelegate {

    var onError = { err: Int -> Unit }

    var onWarning = { warn: Int -> Unit }

    var onJoinChannelSuccess = { channel: String?, uid: Int, elapsed: Int -> Unit }

    var onRejoinChannelSuccess = { channel: String?, uid: Int, elapsed: Int -> Unit }

    var onLeaveChannel = { stats: IRtcEngineEventHandler.RtcStats? -> Unit }

    var onClientRoleChanged = { oldRole: Int, newRole: Int -> Unit }

    var onLocalUserRegistered = { uid: Int, userAccount: String? -> Unit }

    var onUserInfoUpdated = { uid: Int, userInfo: UserInfo? -> Unit }

    var onUserJoined = { uid: Int, elapsed: Int -> Unit }

    var onUserOffline = { uid: Int, reason: Int -> Unit }

    var onProxyConnected = { channel: String?,
                             uid: Int,
                             proxyType: Int,
                             localProxyIp: String?,
                             elapsed: Int ->
        Unit
    }

    var onConnectionStateChanged = { state: Int, reason: Int -> Unit }

    @Deprecated("")
    var onConnectionInterrupted = { -> Unit }

    var onConnectionLost = { -> Unit }

    @Deprecated("")
    var onConnectionBanned = { -> Unit }

    var onApiCallExecuted = { error: Int, api: String?, result: String? -> Unit }

    var onTokenPrivilegeWillExpire = { token: String? -> Unit }

    var onRequestToken = { -> Unit }

    @Deprecated("")
    var onMicrophoneEnabled = { enabled: Boolean -> Unit }

    var onAudioVolumeIndication =
        { speakers: Array<out IRtcEngineEventHandler.AudioVolumeInfo>?, totalVolume: Int -> Unit }

    var onLocalVoicePitchInHz = { pitchInHz: Int -> Unit }

    var onActiveSpeaker = { uid: Int -> Unit }

    @Deprecated("")
    var onFirstLocalAudioFrame = { elapsed: Int -> Unit }

    var onFirstLocalAudioFramePublished = { elapsed: Int -> Unit }


    @Deprecated("")
    var onFirstRemoteAudioFrame = { uid: Int, elapsed: Int -> Unit }

    @Deprecated("")
    var onVideoStopped = { -> Unit }

    var onFirstLocalVideoFrame = { width: Int, height: Int, elapsed: Int -> Unit }

    var onFirstLocalVideoFramePublished = { elapsed: Int -> Unit }

    var onFirstRemoteVideoDecoded = { uid: Int, width: Int, height: Int, elapsed: Int -> Unit }

    var onFirstRemoteVideoFrame = { uid: Int, width: Int, height: Int, elapsed: Int -> Unit }

    var onUserMuteAudio = { uid: Int, muted: Boolean -> Unit }

    var onUserMuteVideo = { uid: Int, muted: Boolean -> Unit }

    var onUserEnableVideo = { uid: Int, enabled: Boolean -> Unit }

    var onUserEnableLocalVideo = { uid: Int, enabled: Boolean -> Unit }

    var onVideoSizeChanged = { uid: Int, width: Int, height: Int, rotation: Int -> Unit }

    var onRemoteAudioStateChanged = { uid: Int, state: Int, reason: Int, elapsed: Int -> Unit }

    var onAudioPublishStateChanged = { channel: String?,
                                       oldState: Int,
                                       newState: Int,
                                       elapseSinceLastState: Int ->
        Unit
    }

    var onVideoPublishStateChanged = { channel: String?,
                                       oldState: Int,
                                       newState: Int,
                                       elapseSinceLastState: Int ->
        Unit
    }

    var onAudioSubscribeStateChanged = { channel: String?,
                                         uid: Int,
                                         oldState: Int,
                                         newState: Int,
                                         elapseSinceLastState: Int ->
        Unit
    }

    var onVideoSubscribeStateChanged = { channel: String?,
                                         uid: Int,
                                         oldState: Int,
                                         newState: Int,
                                         elapseSinceLastState: Int ->
        Unit
    }

    var onRemoteVideoStateChanged = { uid: Int, state: Int, reason: Int, elapsed: Int -> Unit }

    var onUserSuperResolutionEnabled = { uid: Int, enabled: Boolean, reason: Int -> Unit }

    var onVirtualBackgroundSourceEnabled = { enabled: Boolean, reason: Int -> Unit }

    var onContentInspectResult = { result: Int -> Unit }

    var onSnapshotTaken = { channel: String?,
                            uid: Int,
                            filePath: String?,
                            width: Int,
                            height: Int,
                            errCode: Int ->
        Unit
    }

    var onChannelMediaRelayStateChanged = { state: Int, code: Int -> Unit }

    var onChannelMediaRelayEvent = { code: Int -> Unit }

    var onLocalPublishFallbackToAudioOnly = { isFallbackOrRecover: Boolean -> Unit }

    var onRemoteSubscribeFallbackToAudioOnly = { uid: Int, isFallbackOrRecover: Boolean -> Unit }

    var onAudioRouteChanged = { routing: Int -> Unit }

    @Deprecated("")
    var onCameraReady = { -> Unit }


    var onCameraFocusAreaChanged = { rect: Rect? -> Unit }

    var onCameraExposureAreaChanged = { rect: Rect? -> Unit }

    var onFacePositionChanged = { imageWidth: Int,
                                  imageHeight: Int,
                                  faces: Array<out IRtcEngineEventHandler.AgoraFacePositionInfo>? ->
        Unit
    }


    @Deprecated("")
    var onAudioQuality = { uid: Int, quality: Int, delay: Short, lost: Short -> Unit }

    var onRtcStats = { stats: IRtcEngineEventHandler.RtcStats? -> Unit }

    var onLastmileQuality = { quality: Int -> Unit }

    var onLastmileProbeResult = { result: IRtcEngineEventHandler.LastmileProbeResult? -> Unit }

    var onNetworkQuality = { uid: Int, txQuality: Int, rxQuality: Int -> Unit }

    var onWlAccMessage = { reason: Int, action: Int, wlAccMsg: String? -> Unit }

    var onWlAccStats =
        { currentStats: IRtcEngineEventHandler.WlAccStats?, averageStats: IRtcEngineEventHandler.WlAccStats? -> Unit }

    var onLocalVideoStats = { stats: IRtcEngineEventHandler.LocalVideoStats? -> Unit }

    var onRemoteVideoStats = { stats: IRtcEngineEventHandler.RemoteVideoStats? -> Unit }

    var onLocalAudioStats = { stats: IRtcEngineEventHandler.LocalAudioStats? -> Unit }

    var onRemoteAudioStats = { stats: IRtcEngineEventHandler.RemoteAudioStats? -> Unit }

    @Deprecated("")
    var onLocalVideoStat = { sentBitrate: Int, sentFrameRate: Int -> Unit }


    @Deprecated("")
    var onRemoteVideoStat = { uid: Int,
                              delay: Int,
                              receivedBitrate: Int,
                              receivedFrameRate: Int ->
        Unit
    }

    @Deprecated("")
    var onRemoteAudioTransportStats = { uid: Int, delay: Int, lost: Int, rxKBitRate: Int -> Unit }


    @Deprecated("")
    var onRemoteVideoTransportStats = { uid: Int, delay: Int, lost: Int, rxKBitRate: Int -> Unit }

    var onAudioMixingStateChanged = { state: Int, reason: Int -> Unit }

    @Deprecated("")
    var onAudioMixingFinished = { -> Unit }

    var onAudioEffectFinished = { soundId: Int -> Unit }

    @Deprecated("")
    var onFirstRemoteAudioDecoded = { uid: Int, elapsed: Int -> Unit }

    var onLocalAudioStateChanged = { state: Int, error: Int -> Unit }

    var onRequestAudioFileInfo = { info: IRtcEngineEventHandler.AudioFileInfo?, error: Int -> Unit }

    var onLocalVideoStateChanged = { localVideoState: Int, error: Int -> Unit }

    var onRtmpStreamingStateChanged = { url: String?, state: Int, errCode: Int -> Unit }

    @Deprecated("")
    var onStreamPublished = { url: String?, error: Int -> Unit }

    @Deprecated("")
    var onStreamUnpublished = { url: String? -> Unit }

    var onTranscodingUpdated = { -> Unit }

    var onRtmpStreamingEvent = { url: String?, error: Int -> Unit }

    var onStreamInjectedStatus = { url: String?, uid: Int, status: Int -> Unit }

    var onStreamMessage = { uid: Int, streamId: Int, data: ByteArray? -> Unit }

    var onStreamMessageError = { uid: Int,
                                 streamId: Int,
                                 error: Int,
                                 missed: Int,
                                 cached: Int ->
        Unit
    }

    var onMediaEngineLoadSuccess = { -> Unit }

    var onMediaEngineStartCallSuccess = { -> Unit }

    var onNetworkTypeChanged = { type: Int -> Unit }

    var onUploadLogResult = { requestId: String?, success: Boolean, reason: Int -> Unit }

    var onClientRoleChangeFailed = { reason: Int, currentRole: Int -> Unit }
}

