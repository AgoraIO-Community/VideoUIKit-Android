package io.agora.agorauikit_android

import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
open class AgoraRtmChannelHandler(private val hostView: AgoraVideoViewer) : RtmChannelListener {
    override fun onMemberCountUpdated(memberCount: Int) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM member count updated : $memberCount")
    }

    override fun onAttributesUpdated(attributeList: MutableList<RtmChannelAttribute>?) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel attributes updated")
    }

    override fun onMessageReceived(
        rtmMessage: RtmMessage,
        rtmChannelMember: RtmChannelMember
    ) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel Message Received")
        AgoraRtmController.Companion.messageReceived(message = rtmMessage.text, hostView = hostView)
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
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member : ${rtmChannelMember.userId}  joined channel : ${rtmChannelMember.channelId}")
//        sendUserData(toChannel = false, peerRtmId = rtmChannelMember.userId)
        AgoraRtmController.Companion.sendUserData(toChannel = false, peerRtmId = rtmChannelMember.userId, hostView = this.hostView)
    }

    override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member left ${rtmChannelMember.userId} from channel ${rtmChannelMember.channelId}")
    }
}