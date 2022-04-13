package io.agora.agorauikit_android

import io.agora.rtm.*

@ExperimentalUnsignedTypes
class AgoraRtmClientHandler(private val hostView: AgoraVideoViewer) : RtmClientListener {
    override fun onConnectionStateChanged(state: Int, reason: Int) {
        println("RTM Connection State Changed. state: $state, reason: $reason")
    }

    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
        AgoraRtmController.Companion.messageReceived(message = rtmMessage.text, hostView = hostView)
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
}