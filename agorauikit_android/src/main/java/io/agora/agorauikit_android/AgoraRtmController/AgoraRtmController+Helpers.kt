package io.agora.agorauikit_android.AgoraRtmController

import android.graphics.Color
import com.google.android.material.snackbar.Snackbar
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraVideoViewer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject

@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.messageReceived(message: String, hostView: AgoraVideoViewer) {
    val messageMap = JSONObject(message)
    when (messageMap.getString("messageType")) {
        "UserData" -> {
            val userData = Json.decodeFromString<UserData>(message)
            val rtmId = userData.rtmId
            userData.rtcId?.let { rtcId ->
                hostView.agoraSettings.uidToUserIdMap.putIfAbsent(rtcId, rtmId)
            }
            hostView.agoraSettings.userRtmMap.putIfAbsent(rtmId, userData)
        }
        "MuteRequest" -> {
            val muteRequest = Json.decodeFromString<MuteRequest>(message)
            val deviceType = DeviceType.fromInt(muteRequest.device)
            val snackbar = Snackbar.make(
                hostView,
                if (deviceType == DeviceType.MIC)
                    "Please " + (if (muteRequest.mute) "" else "un") + "mute your mic"
                else
                    "Please " + (if (muteRequest.mute) "dis" else "en") + "able your camera",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction(
                if (deviceType == DeviceType.MIC)
                    if (muteRequest.mute) "mute" else "unmute"
                else if (muteRequest.mute) "disable" else "enable"
            ) {
                var changingButton: AgoraButton?
                var isMuted = muteRequest.mute
                if (deviceType == DeviceType.MIC) {
                    hostView.agkit.muteLocalAudioStream(isMuted)
                    changingButton = hostView.micButton
                    hostView.userVideoLookup[hostView.userID]?.audioMuted = isMuted
                } else {
                    hostView.agkit.enableLocalVideo(!isMuted)
                    changingButton = hostView.camButton
                    hostView.userVideoLookup[hostView.userID]?.videoMuted = isMuted
                }
                changingButton?.isSelected = isMuted
                changingButton?.background?.setTint(if (isMuted) Color.RED else Color.GRAY)
            }
            snackbar.show()
        }
    }
}
