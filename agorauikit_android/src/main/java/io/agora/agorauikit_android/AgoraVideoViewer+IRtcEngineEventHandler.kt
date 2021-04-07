package io.agora.agorauikit_android
import android.app.Activity
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
open class AgoraVideoViewerHandler(private val hostView: AgoraVideoViewer) : IRtcEngineEventHandler() {
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
    }
    override fun onUserJoined(uid: Int, elapsed: Int) {
        super.onUserJoined(uid, elapsed)
        this.hostView.remoteUserIDs.add(uid)
    }

    override fun onRemoteAudioStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
        super.onRemoteAudioStateChanged(uid, state, reason, elapsed)
        Logger.getLogger("AgoraUIKit").log(Level.WARNING, "setting muted state: " + state)
        (this.hostView.context as Activity).runOnUiThread {
            if (state == Constants.REMOTE_AUDIO_STATE_STOPPED || state == Constants.REMOTE_AUDIO_STATE_STARTING || state == Constants.REMOTE_VIDEO_STATE_DECODING) {
                if (state == Constants.REMOTE_AUDIO_STATE_STARTING && !this.hostView.userVideoLookup.containsKey(uid)) {
                    this.hostView.addUserVideo(uid)
                }
                if (this.hostView.userVideoLookup.containsKey(uid)) {
                    this.hostView.userVideoLookup[uid]?.audioMuted = state == Constants.REMOTE_AUDIO_STATE_STOPPED
                }
            }
        }
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        super.onUserOffline(uid, reason)
        Logger.getLogger("AgoraUIKit").log(Level.WARNING, "User offlined: $reason")
        if (reason == Constants.USER_OFFLINE_QUIT || reason == Constants.USER_OFFLINE_DROPPED) {
            this.hostView.remoteUserIDs.remove(uid)
        }
        if (this.hostView.userVideoLookup.containsKey(uid)) {
            (this.hostView.context as Activity).runOnUiThread {
                this.hostView.removeUserVideo(uid)
            }
        }
    }

    override fun onActiveSpeaker(uid: Int) {
        super.onActiveSpeaker(uid)
        this.hostView.activeSpeaker = uid
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
    }

    override fun onFirstLocalAudioFramePublished(elapsed: Int) {
        super.onFirstLocalAudioFramePublished(elapsed)
//        this.hostView.addLocalVideo()?.audioMuted = false
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        super.onJoinChannelSuccess(channel, uid, elapsed)
        this.hostView.connectionData.channel = channel
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "join channel success")
        this.hostView.userID = uid
        if (this.hostView.userRole == Constants.CLIENT_ROLE_BROADCASTER) {
            (this.hostView.context as Activity).runOnUiThread(Runnable {
                this.hostView.addLocalVideo()
            })
        }
    }

    override fun onTokenPrivilegeWillExpire(token: String?) {
        super.onTokenPrivilegeWillExpire(token)
        this.hostView.fetchRenewToken()
    }

    override fun onRequestToken() {
        super.onRequestToken()
        this.hostView.fetchRenewToken()
    }
}
