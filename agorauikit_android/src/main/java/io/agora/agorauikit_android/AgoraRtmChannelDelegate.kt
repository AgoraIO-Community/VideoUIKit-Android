package io.agora.agorauikit_android

import io.agora.rtm.*

open class AgoraRtmChannelDelegate {

    var onMemberCountUpdated = { memberCount: Int -> Unit }

    var onAttributesUpdated = { attributeList: MutableList<RtmChannelAttribute>? -> Unit }

    var onMessageReceived = { rtmMessage: RtmMessage,
                              rtmChannelMember: RtmChannelMember ->
        Unit
    }

    var onImageMessageReceived = { p0: RtmImageMessage?,
                                   p1: RtmChannelMember? ->
        Unit
    }


    var onFileMessageReceived = { p0: RtmFileMessage?, p1: RtmChannelMember? -> Unit }

    var onMemberJoined = { rtmChannelMember: RtmChannelMember -> Unit }

    var onMemberLeft = { rtmChannelMember: RtmChannelMember -> Unit }
}