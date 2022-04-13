package io.agora.agorauikit_android

import android.graphics.Color
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.json.JSONObject


@ExperimentalUnsignedTypes
fun AgoraRtmController.Companion.messageReceived(message: String, hostView: AgoraVideoViewer) {
    val messageMap = JSONObject(message)
    when (messageMap.getString("messageType")) {
        "UserData" -> {
            val rtcId = messageMap.getInt("rtcId")
            val rtmId = messageMap.getString("rtmId")
            addToUidToUserIdMap(
                rtcId = rtcId,
                rtmId = rtmId,
                uidToUserIdMap = hostView.agoraSettings.uidToUserIdMap
            )
            addToUserRtmMap(
                rtmId = rtmId,
                message = message,
                userRtmMap = hostView.agoraSettings.userRtmMap
            )
        }
        "MuteRequest" -> {
            var deviceStatus = messageMap.getBoolean("mute")
            val deviceType = messageMap.getInt("device")
            val snackbar = Snackbar.make(
                hostView,
                if (deviceType == 0)
                    "Please " + if (deviceStatus) "unmute" else "mute" + " your mic"
                else
                    "Please " + if (deviceStatus) "enable" else "disable" + " your camera",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction(
                if (deviceType == 0)
                    if (deviceStatus) "unmute" else "mute"
                else
                    if (deviceStatus) "enable" else "disable"
            ){
                if (deviceType == 0) {
                    hostView.agkit.muteLocalAudioStream(!deviceStatus);
                    deviceStatus = !deviceStatus
                    hostView.micButton?.background?.setTint(if (deviceStatus) Color.RED else Color.GRAY)
                    hostView.micButton?.isSelected = deviceStatus
                    hostView.userVideoLookup[hostView.userID]?.mutedFlag?.visibility =
                        if (deviceStatus) FrameLayout.VISIBLE else FrameLayout.INVISIBLE
                    hostView.userVideoLookup[hostView.userID]?.audioMuted = deviceStatus
                } else {
                    hostView.agkit.enableLocalVideo(deviceStatus)
                    deviceStatus = !deviceStatus
                    hostView.camButton?.background?.setTint(if (deviceStatus) Color.RED else Color.GRAY)
                    hostView.camButton?.isSelected = deviceStatus
                    hostView.userVideoLookup[hostView.userID]?.backgroundView?.visibility =
                        if (deviceStatus) FrameLayout.VISIBLE else FrameLayout.INVISIBLE
                    hostView.userVideoLookup[hostView.userID]?.videoMuted = !deviceStatus
                }
            }
            snackbar.show()
        }
//        "CameraRequest" -> {
//            var camStatus = messageMap.getBoolean("mute")
//            val snackbar = Snackbar.make(
//                hostView,
//                "Please " + if (camStatus) "enable" else "disable" + " your camera",
//                Snackbar.LENGTH_LONG
//            )
//            snackbar.setAction(if (camStatus) "enable" else "disable") {
//                hostView.agkit.enableLocalVideo(camStatus)
//                camStatus = !camStatus
//                hostView.camButton?.background?.setTint(if (camStatus) Color.RED else Color.GRAY)
//                hostView.camButton?.isSelected = camStatus
//                hostView.userVideoLookup[hostView.userID]?.backgroundView?.visibility =
//                    if (camStatus) FrameLayout.VISIBLE else FrameLayout.INVISIBLE
//                hostView.userVideoLookup[hostView.userID]?.videoMuted = !camStatus
//            }
//            snackbar.show()
//        }
    }
}

private fun addToUidToUserIdMap(
    rtcId: Int,
    rtmId: String,
    uidToUserIdMap: MutableMap<Int, String>
) {
    uidToUserIdMap.putIfAbsent(rtcId, rtmId)
}

private fun addToUserRtmMap(
    rtmId: String,
    message: String,
    userRtmMap: MutableMap<String, String>
) {
    userRtmMap.putIfAbsent(rtmId, message)
}