package io.agora.agorauikit_android.AgoraRtmController

import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.rtm.RtmClientListener
import io.agora.rtm.RtmMessage
import java.util.logging.Level
import java.util.logging.Logger
/**
 * Class for all the Agora RTM Client event handlers
 *
 * @param hostView [AgoraVideoViewer]
 */
@ExperimentalUnsignedTypes
class AgoraRtmClientHandler(private val hostView: AgoraVideoViewer) : RtmClientListener {
    override fun onConnectionStateChanged(state: Int, reason: Int) {
        Logger.getLogger("AgoraVideoUIKit")
            .log(Level.INFO, "RTM Connection State Changed. state: $state, reason: $reason")

        this.hostView.rtmClientOverrideHandler?.onConnectionStateChanged(state, reason)
    }

    override fun onMessageReceived(rtmMessage: RtmMessage, peerId: String?) {
        AgoraRtmController.messageReceived(message = rtmMessage.text, hostView = hostView)

        this.hostView.rtmClientOverrideHandler?.onMessageReceived(rtmMessage, peerId)
    }

    override fun onTokenExpired() {
        this.hostView.rtmClientOverrideHandler?.onTokenExpired()
    }

    override fun onTokenPrivilegeWillExpire() {
        this.hostView.rtmClientOverrideHandler?.onTokenPrivilegeWillExpire()
    }

    override fun onPeersOnlineStatusChanged(peerStatus: MutableMap<String, Int>?) {
        Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "onPeerOnlineStatusChanged: $peerStatus")

        this.hostView.rtmClientOverrideHandler?.onPeersOnlineStatusChanged(peerStatus)
    }
}
