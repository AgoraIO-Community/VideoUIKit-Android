package io.agora.agorauikit_android.AgoraRtmController

import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmFileMessage
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmMediaOperationProgress
import io.agora.rtm.RtmMessage
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
class AgoraRtmClientHandler(private val hostView: AgoraVideoViewer) : RtmClientListener {
    override fun onConnectionStateChanged(state: Int, reason: Int) {
        Logger.getLogger("AgoraUIKit")
            .log(Level.INFO, "RTM Connection State Changed. state: $state, reason: $reason")

        this.hostView.rtmClientOverrideHandler?.onConnectionStateChanged(state, reason)
    }

    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
        AgoraRtmController.messageReceived(message = rtmMessage.text, hostView = hostView)

        this.hostView.rtmClientOverrideHandler?.onMessageReceived(rtmMessage, peerId)
    }

    override fun onImageMessageReceivedFromPeer(p0: RtmImageMessage?, p1: String?) {
        this.hostView.rtmClientOverrideHandler?.onImageMessageReceivedFromPeer(p0, p1)
    }

    override fun onFileMessageReceivedFromPeer(p0: RtmFileMessage?, p1: String?) {
        this.hostView.rtmClientOverrideHandler?.onFileMessageReceivedFromPeer(p0, p1)
    }

    override fun onMediaUploadingProgress(
        p0: RtmMediaOperationProgress?,
        p1: Long
    ) {
        this.hostView.rtmClientOverrideHandler?.onMediaUploadingProgress(p0, p1)
    }

    override fun onMediaDownloadingProgress(
        p0: RtmMediaOperationProgress?,
        p1: Long
    ) {
        this.hostView.rtmClientOverrideHandler?.onMediaDownloadingProgress(p0, p1)
    }

    override fun onTokenExpired() {
        this.hostView.rtmClientOverrideHandler?.onTokenExpired()
    }

    override fun onPeersOnlineStatusChanged(peerStatus: MutableMap<String, Int>?) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "onPeerOnlineStatusChanged: $peerStatus")

        this.hostView.rtmClientOverrideHandler?.onPeersOnlineStatusChanged(peerStatus)
    }
}
