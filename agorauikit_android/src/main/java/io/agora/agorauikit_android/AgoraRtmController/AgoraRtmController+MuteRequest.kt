package io.agora.agorauikit_android.AgoraRtmController

import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.rtc2.RtcEngine
import io.agora.rtm.ErrorInfo
import io.agora.rtm.ResultCallback
import io.agora.rtm.RtmClient
import io.agora.rtm.RtmMessage
import io.agora.rtm.SendMessageOptions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.logging.Level
import java.util.logging.Logger

@Serializable
enum class DeviceType(val raw: Int) {
    CAMERA(0), MIC(1);
    companion object {
        fun fromInt(value: Int) = DeviceType.values().first { it.raw == value }
    }
}

@Serializable
data class UserData(
    @SerialName("messageType") var messageType: String = "UserData",
    @SerialName("rtmId") var rtmId: String,
    @SerialName("rtcId") var rtcId: Int?,
    @SerialName("username") var username: String? = null,
    @SerialName("role") var role: Int,
    @SerialName("agora") var agoraVersion: AgoraVersion,
    @SerialName("uikit") var uiKitData: UIKitData,
) : java.io.Serializable

@Serializable
data class AgoraVersion(
    @SerialName("rtm") var rtmVersion: String,
    @SerialName("rtc") var rtcVersion: String,
) : java.io.Serializable {
    companion object {
        val current: AgoraVersion = AgoraVersion(RtmClient.getSdkVersion(), RtcEngine.getSdkVersion())
    }
}

@Serializable
data class UIKitData(
    @SerialName("platform") var platform: String,
    @SerialName("framework") var framework: String,
    @SerialName("version") var version: String,
) : java.io.Serializable {
    companion object {
        val current: UIKitData = UIKitData("android", "native", "4.0.1")
    }
}

@Serializable
data class MuteRequest(
    @SerialName("messageType") var messageType: String = "MuteRequest",
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

    val userData = UserData(
        rtmId = rtmId,
        rtcId = hostView.userID,
        username = hostView.connectionData.username,
        role = hostView.userRole,
        agoraVersion = AgoraVersion.current,
        uiKitData = UIKitData.current
    )

    val json = Json { encodeDefaults = true }
    val data = json.encodeToString(userData)
    val message: RtmMessage = hostView.agRtmClient.createMessage(data)

    Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, message.text)

    val option = SendMessageOptions()

    if (!toChannel) {
        hostView.agRtmClient.sendMessageToPeer(
            peerRtmId,
            message,
            option,
            object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "UserData message sent to $peerRtmId")
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "Failed to send UserData message to $peerRtmId")
                }
            }
        )
    } else {
        hostView.agRtmChannel.sendMessage(
            message, option,
            object : ResultCallback<Void> {
                override fun onSuccess(p0: Void?) {
                    Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "UserData message sent to channel")
                }

                override fun onFailure(p0: ErrorInfo?) {
                    Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "Failed to send UserData message to channel")
                }
            }
        )
    }
}

@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.sendMuteRequest(
    peerRtcId: Int,
    mute: Boolean,
    hostView: AgoraVideoViewer,
    deviceType: DeviceType,
    isForceful: Boolean = false
) {
    var peerRtmId: String? = null

    val muteRequest = MuteRequest(
        rtcId = peerRtcId,
        mute = mute,
        device = deviceType.raw,
        isForceful = isForceful
    )

    val json = Json { encodeDefaults = true }
    val data = json.encodeToString(muteRequest)
    val message: RtmMessage = hostView.agRtmClient.createMessage(data)

    val option = SendMessageOptions()

    if (peerRtcId == hostView.userID) {
        Logger.getLogger("AgoraVideoUIKit").log(Level.SEVERE, "Can't send message to local user")
    } else {
        if (hostView.agoraSettings.uidToUserIdMap.containsKey(peerRtcId)) {
            peerRtmId = hostView.agoraSettings.uidToUserIdMap.getValue(peerRtcId)

            hostView.agRtmClient.sendMessageToPeer(
                peerRtmId,
                message,
                option,
                object : ResultCallback<Void> {
                    override fun onSuccess(p0: Void?) {
                        Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "Mute Request sent to $peerRtmId")
                    }

                    override fun onFailure(p0: ErrorInfo?) {
                        Logger.getLogger("AgoraVideoUIKit").log(Level.INFO, "Failed to send Mute Request to $peerRtmId. Error $p0")
                    }
                }
            )
        }
    }
}
