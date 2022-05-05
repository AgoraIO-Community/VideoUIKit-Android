package io.agora.agorauikit_android

import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
class AgoraRtmClientHandler(private val hostView: AgoraVideoViewer) : RtmClientListener {
    override fun onConnectionStateChanged(state: Int, reason: Int) {
        Logger.getLogger("AgoraUIKit")
            .log(Level.INFO, "RTM Connection State Changed. state: $state, reason: $reason")

        this.hostView.agoraRtmClientDelegate?.onConnectionStateChanged?.invoke(state, reason)
    }

    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
        AgoraRtmController.Companion.messageReceived(message = rtmMessage.text, hostView = hostView)

        this.hostView.agoraRtmClientDelegate?.onMessageReceived?.invoke(rtmMessage, peerId)
    }

    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
        this.hostView.agoraRtmClientDelegate?.onImageMessageReceivedFromPeer?.invoke(p0, p1)
    }

    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
        this.hostView.agoraRtmClientDelegate?.onFileMessageReceivedFromPeer?.invoke(p0, p1)
    }

    override fun onMediaUploadingProgress(
        p0: RtmMediaOperationProgress?,
        p1: Long
    ) {
        this.hostView.agoraRtmClientDelegate?.onMediaUploadingProgress?.invoke(p0, p1)
    }

    override fun onMediaDownloadingProgress(
        p0: RtmMediaOperationProgress?,
        p1: Long
    ) {
        this.hostView.agoraRtmClientDelegate?.onMediaDownloadingProgress?.invoke(p0, p1)
    }

    override fun onTokenExpired() {
        this.hostView.agoraRtmClientDelegate?.onTokenExpired?.invoke()
    }

    override fun onPeersOnlineStatusChanged(peerStatus: MutableMap<String, Int>?) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "onPeerOnlineStatusChanged: $peerStatus")

        this.hostView.agoraRtmClientDelegate?.onPeersOnlineStatusChanged?.invoke(peerStatus)
    }
}