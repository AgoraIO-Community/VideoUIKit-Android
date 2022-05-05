package io.agora.agorauikit_android

import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
open class AgoraRtmChannelHandler(private val hostView: AgoraVideoViewer) : RtmChannelListener {
    override fun onMemberCountUpdated(memberCount: Int) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM member count updated : $memberCount")

        this.hostView.agoraRtmChannelDelegate?.onMemberCountUpdated?.invoke(memberCount)
    }

    override fun onAttributesUpdated(attributeList: MutableList<RtmChannelAttribute>?) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel attributes updated")

        this.hostView.agoraRtmChannelDelegate?.onAttributesUpdated?.invoke(attributeList)
    }

    override fun onMessageReceived(
        rtmMessage: RtmMessage,
        rtmChannelMember: RtmChannelMember
    ) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel Message Received")
        AgoraRtmController.Companion.messageReceived(message = rtmMessage.text, hostView = hostView)

        this.hostView.agoraRtmChannelDelegate?.onMessageReceived?.invoke(rtmMessage, rtmChannelMember)
    }

    override fun onImageMessageReceived(
        p0: RtmImageMessage?,
        p1: RtmChannelMember?
    ) {
        this.hostView.agoraRtmChannelDelegate?.onImageMessageReceived?.invoke(p0, p1)
    }

    override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
        this.hostView.agoraRtmChannelDelegate?.onFileMessageReceived?.invoke(p0, p1)
    }

    override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member : ${rtmChannelMember.userId}  joined channel : ${rtmChannelMember.channelId}")
        AgoraRtmController.Companion.sendUserData(toChannel = false, peerRtmId = rtmChannelMember.userId, hostView = this.hostView)

        this.hostView.agoraRtmChannelDelegate?.onMemberJoined?.invoke(rtmChannelMember)
    }

    override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member left ${rtmChannelMember.userId} from channel ${rtmChannelMember.channelId}")

        this.hostView.agoraRtmChannelDelegate?.onMemberLeft?.invoke(rtmChannelMember)
    }
}