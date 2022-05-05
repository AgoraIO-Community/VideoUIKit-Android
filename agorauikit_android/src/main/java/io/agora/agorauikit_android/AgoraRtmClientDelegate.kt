package io.agora.agorauikit_android

import io.agora.rtm.RtmFileMessage
import io.agora.rtm.RtmImageMessage
import io.agora.rtm.RtmMediaOperationProgress
import io.agora.rtm.RtmMessage

open class AgoraRtmClientDelegate {
    var onConnectionStateChanged = { state: Int, reason: Int -> Unit }

    var onMessageReceived = { rtmMessage: RtmMessage, peerId: String? -> Unit }

    var onImageMessageReceivedFromPeer = { p0: RtmImageMessage?, p1: String? -> Unit }

    var onFileMessageReceivedFromPeer = { p0: RtmFileMessage?, p1: String? -> Unit }

    var onMediaUploadingProgress = { p0: RtmMediaOperationProgress?,
                                     p1: Long ->
        Unit
    }

    var onMediaDownloadingProgress = { p0: RtmMediaOperationProgress?,
                                       p1: Long ->
        Unit
    }

    var onTokenExpired = { -> Unit }

    var onPeersOnlineStatusChanged = { peerStatus: MutableMap<String, Int>? -> Unit }
}