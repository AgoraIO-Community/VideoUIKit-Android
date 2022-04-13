package io.agora.agorauikit_android

import io.agora.rtc.RtcEngine
import io.agora.rtm.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger


enum class DeviceType(val raw: Int) {
    CAMERA(1), MIC(0)
}


@Serializable
data class UserData(
    @SerialName("messageType") var messageType: String,
    @SerialName("rtmId") var rtmId: String,
    @SerialName("rtcId") var rtcId: Int,
    @SerialName("username") var username: String,
    @SerialName("role") var role: Int,
    @SerialName("agora") var agoraVersion: AgoraVersion,
    @SerialName("uikit") var uiKitData: UIKitData,
) : java.io.Serializable

@Serializable
data class AgoraVersion(
    @SerialName("rtm") var rtmVersion: String,
    @SerialName("rtc") var rtcVersion: String,
) : java.io.Serializable

@Serializable
data class UIKitData(
    @SerialName("platform") var platform: String,
    @SerialName("framework") var framework: String,
    @SerialName("version") var version: String,
) : java.io.Serializable

@Serializable
data class MuteRequest(
    @SerialName("messageType") var messageType: String,
    @SerialName("rtcId") var rtcId: Int,
    @SerialName("mute") var mute: Boolean,
    @SerialName("device") var device: Int,
    @SerialName("isForceful") var isForceful: Boolean,
) : java.io.Serializable


@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.sendUserData(
    toChannel: Boolean,
    peerRtmId: String? = null,
    hostView: AgoraVideoViewer
) {
    val rtmId = hostView.connectionData.rtmId as String

    val agoraVersion = AgoraVersion(
        rtmVersion = RtmClient.getSdkVersion(),
        rtcVersion = RtcEngine.getSdkVersion(),
    )

    val uikitData = UIKitData(
        platform = "android",
        framework = "native",
        version = "1.0.0",
    )

    val userData = UserData(
        messageType = "UserData",
        rtmId = rtmId,
        rtcId = hostView.userID,
        username = hostView.connectionData.username?.let { hostView.connectionData.username } ?: let { "" },
        role = hostView.userRole,
        agoraVersion = agoraVersion,
        uiKitData = uikitData
    )

    val data = Json.encodeToString(userData)
    val message: RtmMessage = hostView.agRtmClient.createMessage()
    message.text = data

    val option = SendMessageOptions()
    option.enableOfflineMessaging = true

    if (!toChannel) {
        hostView.agRtmClient.sendMessageToPeer(
            peerRtmId,
            message,
            option,
            object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    Logger.getLogger("AgoraUIKit").log(Level.INFO, "UserData message sent to $peerRtmId")
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Logger.getLogger("AgoraUIKit").log(Level.INFO, "Failed to send UserData message to $peerRtmId")
                }
            })
    } else {
        hostView.agRtmChannel.sendMessage(message, option, object : ResultCallback<Void> {
            override fun onSuccess(p0: Void?) {
                Logger.getLogger("AgoraUIKit").log(Level.INFO, "UserData message sent to channel")
            }

            override fun onFailure(p0: ErrorInfo?) {
                Logger.getLogger("AgoraUIKit").log(Level.INFO, "Failed to send UserData message to channel")
            }
        })
    }
}

@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.sendMuteRequest(
    peerRtcId: Int,
    isMicEnabled: Boolean,
    hostView: AgoraVideoViewer,
    deviceType: DeviceType
) {
    var peerRtmId: String? = null

    val muteRequest = MuteRequest(
        messageType = "MuteRequest",
        rtcId = peerRtcId,
        mute = isMicEnabled,
        device = deviceType.raw,
        isForceful = false
    )

    val data = Json.encodeToString(muteRequest)
    val message: RtmMessage = hostView.agRtmClient.createMessage()
    message.text = data

    val option = SendMessageOptions()
    option.enableOfflineMessaging = true

    if (peerRtcId == hostView.userID) {
        Logger.getLogger("AgoraUIKit").log(Level.SEVERE, "Can't send message to local user")
    } else {
        if (hostView.agoraSettings.uidToUserIdMap.containsKey(peerRtcId)) {
            peerRtmId = hostView.agoraSettings.uidToUserIdMap.getValue(peerRtcId)

            hostView.agRtmClient.sendMessageToPeer(
                peerRtmId,
                message,
                option,
                object : ResultCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        Logger.getLogger("AgoraUIKit").log(Level.INFO, "Mute Request sent to $peerRtmId")
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        Logger.getLogger("AgoraUIKit").log(Level.INFO, "Failed to send Mute Request to $peerRtmId. Error $p0")
                    }
                })
        }
    }
}

