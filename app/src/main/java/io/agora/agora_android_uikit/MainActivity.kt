package io.agora.agora_android_uikit

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.agorauikit_android.*
import io.agora.rtc.Constants
import io.agora.rtc.video.BeautyOptions

// Ask for Android device permissions at runtime.
private const val PERMISSION_REQ_ID = 22
private val REQUESTED_PERMISSIONS = arrayOf<String>(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
@ExperimentalUnsignedTypes
class MainActivity : AppCompatActivity() {
    var agView: AgoraVideoViewer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        agView = AgoraVideoViewer(
            this, AgoraConnectionData("my-app-id"),
            agoraSettings=this.settingsWithExtraButtons()
        )
        val set = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

        this.addContentView(agView, set)
        agView!!.join("test", role=Constants.CLIENT_ROLE_BROADCASTER)
    }

    fun settingsWithExtraButtons(): AgoraSettings {
        val agoraSettings = AgoraSettings()

        val beautyOptions = BeautyOptions()
        beautyOptions.smoothnessLevel = 1f
        beautyOptions.rednessLevel = 0.1f

        val agBeautyButton = AgoraButton(this)
        agBeautyButton.clickAction = {
            it.isSelected = !it.isSelected
            agBeautyButton.setImageResource(
                if (it.isSelected) android.R.drawable.star_on else android.R.drawable.star_off
            )
            it.background.setTint(if (it.isSelected) Color.GREEN else Color.GRAY)
            this.agView?.agkit?.setBeautyEffectOptions(it.isSelected, beautyOptions)
        }
        agBeautyButton.setImageResource(android.R.drawable.star_off)

        val hangupButton = AgoraButton(this)
        hangupButton.clickAction = {
            this.agView?.leaveChannel()
        }
        hangupButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        hangupButton.background.setTint(Color.RED)

        agoraSettings.extraButtons = mutableListOf(agBeautyButton, hangupButton)

        return agoraSettings
    }
}