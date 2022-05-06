package io.agora.agorauikit_android

import io.agora.rtm.*
import java.util.logging.Level
import java.util.logging.Logger

@ExperimentalUnsignedTypes
open class AgoraRtmChannelHandler(private val hostView: AgoraVideoViewer) : RtmChannelListener {
    override fun onMemberCountUpdated(memberCount: Int) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM member count updated : $memberCount")
        this.hostView.rtmChannelOverrideHandler?.onMemberCountUpdated(memberCount)
    }

    override fun onAttributesUpdated(attributeList: MutableList<RtmChannelAttribute>?) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel attributes updated")
        this.hostView.rtmChannelOverrideHandler?.onAttributesUpdated(attributeList)
    }

    override fun onMessageReceived(
        rtmMessage: RtmMessage,
        rtmChannelMember: RtmChannelMember
    ) {
        Logger.getLogger("AgoraUIKit").log(Level.INFO, "RTM Channel Message Received")
        AgoraRtmController.Companion.messageReceived(rtmMessage.text, hostView)
        this.hostView.rtmChannelOverrideHandler?.onMessageReceived(rtmMessage, rtmChannelMember)
    }

    override fun onImageMessageReceived(
        p0: RtmImageMessage?,
        p1: RtmChannelMember?
    ) {
        this.hostView.rtmChannelOverrideHandler?.onImageMessageReceived(p0, p1)
    }

    override fun onFileMessageReceived(p0: RtmFileMessage?, p1: RtmChannelMember?) {
        this.hostView.rtmChannelOverrideHandler?.onFileMessageReceived(p0, p1)
    }

    override fun onMemberJoined(rtmChannelMember: RtmChannelMember) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member : ${rtmChannelMember.userId}  joined channel : ${rtmChannelMember.channelId}")
        AgoraRtmController.Companion.sendUserData(toChannel = false, peerRtmId = rtmChannelMember.userId, hostView = this.hostView)

        this.hostView.rtmChannelOverrideHandler?.onMemberJoined(rtmChannelMember)
    }

    override fun onMemberLeft(rtmChannelMember: RtmChannelMember) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "RTM member left ${rtmChannelMember.userId} from channel ${rtmChannelMember.channelId}")

        this.hostView.rtmChannelOverrideHandler?.onMemberLeft(rtmChannelMember)
    }
}