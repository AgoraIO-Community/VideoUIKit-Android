package io.agora.agora_android_uikit

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.agorauikit_android.AgoraButton
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraSettings
import io.agora.agorauikit_android.AgoraVideoViewer
import io.agora.agorauikit_android.requestPermission
import io.agora.rtc.Constants

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
        try {
            agView = AgoraVideoViewer(
                this, AgoraConnectionData("my-app-id"),
                agoraSettings = this.settingsWithExtraButtons(),
            )
        } catch (e: Exception) {
            println("Could not initialise AgoraVideoViewer. Check your App ID is valid. ${e.message}")
            return
        }
        val set = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

        this.addContentView(agView, set)

        // Check that the camera and mic permissions are accepted before attempting to join
        if (AgoraVideoViewer.requestPermission(this)) {
            agView!!.join("test", role = Constants.CLIENT_ROLE_BROADCASTER)
        } else {
            val joinButton = Button(this)
            joinButton.text = "Allow Camera and Microphone, then click here"
            joinButton.setOnClickListener {
                // When the button is clicked, check permissions again and join channel
                // if permissions are granted.
                if (AgoraVideoViewer.requestPermission(this)) {
                    (joinButton.parent as ViewGroup).removeView(joinButton)
                    agView!!.join("test", role = Constants.CLIENT_ROLE_BROADCASTER)
                }
            }
            joinButton.setBackgroundColor(Color.GREEN)
            joinButton.setTextColor(Color.RED)

            this.addContentView(
                joinButton,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 300)
            )
        }
    }

    fun settingsWithExtraButtons(): AgoraSettings {
        val agoraSettings = AgoraSettings()

        val agBeautyButton = AgoraButton(this)
        agBeautyButton.clickAction = {
            it.isSelected = !it.isSelected
            agBeautyButton.setImageResource(
                if (it.isSelected) android.R.drawable.star_on else android.R.drawable.star_off
            )
            it.background.setTint(if (it.isSelected) Color.GREEN else Color.GRAY)
            this.agView?.agkit?.setBeautyEffectOptions(it.isSelected, this.agView?.beautyOptions)
        }
        agBeautyButton.setImageResource(android.R.drawable.star_off)

        agoraSettings.extraButtons = mutableListOf(agBeautyButton)

        return agoraSettings
    }
}
