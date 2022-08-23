package io.agora.agorauikit_android

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

internal class ButtonContainer(context: Context) : LinearLayout(context)

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getControlContainer(): ButtonContainer {
    this.controlContainer?.let {
        return it
    }
    val container = ButtonContainer(context)
    container.visibility = View.VISIBLE
    container.gravity = Gravity.CENTER
    val containerLayout = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200, Gravity.BOTTOM)

    this.addView(container, containerLayout)

    this.controlContainer = container
    return container
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getCameraButton(): AgoraButton {
    this.camButton?.let {
        return it
    }
    val agCamButton = AgoraButton(context = this.context)
    agCamButton.clickAction = {
        (this.context as Activity).runOnUiThread {
            it.isSelected = !it.isSelected
            it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
            this.agkit.enableLocalVideo(!it.isSelected)
        }
    }
    this.camButton = agCamButton
    agCamButton.setImageResource(R.drawable.ic_video_mute)
    return agCamButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getMicButton(): AgoraButton {
    this.micButton?.let {
        return it
    }
    val agMicButton = AgoraButton(context = this.context)
    agMicButton.clickAction = {
        it.isSelected = !it.isSelected
        it.background.setTint(if (it.isSelected) Color.RED else Color.GRAY)
        this.userVideoLookup[this.userID]?.audioMuted = it.isSelected
        this.agkit.muteLocalAudioStream(it.isSelected)
    }
    this.micButton = agMicButton
    agMicButton.setImageResource(android.R.drawable.stat_notify_call_mute)
    return agMicButton
}
@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getFlipButton(): AgoraButton {
    this.flipButton?.let {
        return it
    }
    val agFlipButton = AgoraButton(context = this.context)
    agFlipButton.clickAction = {
        this.agkit.switchCamera()
    }
    this.flipButton = agFlipButton
    agFlipButton.setImageResource(R.drawable.btn_switch_camera)
    return agFlipButton
}
@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getEndCallButton(): AgoraButton {
    this.endCallButton?.let {
        return it
    }
    val hangupButton = AgoraButton(this.context)
    hangupButton.clickAction = {
        this.agkit.stopPreview()
        this.leaveChannel()
    }
    hangupButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
    hangupButton.background.setTint(Color.RED)
    this.endCallButton = hangupButton
    return hangupButton
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.getScreenShareButton(): AgoraButton? {
    return null
}

internal fun AgoraVideoViewer.builtinButtons(): MutableList<AgoraButton> {
    val rtnButtons = mutableListOf<AgoraButton>()
    for (button in this.agoraSettings.enabledButtons) {
        rtnButtons += when (button) {
            AgoraSettings.BuiltinButton.MIC -> this.getMicButton()
            AgoraSettings.BuiltinButton.CAMERA -> this.getCameraButton()
            AgoraSettings.BuiltinButton.FLIP -> this.getFlipButton()
            AgoraSettings.BuiltinButton.END -> this.getEndCallButton()
        }
    }
    return rtnButtons
}

@ExperimentalUnsignedTypes
internal fun AgoraVideoViewer.addVideoButtons() {
    var container = this.getControlContainer()
    val buttons = this.builtinButtons() + this.agoraSettings.extraButtons
    container.visibility = if (buttons.isEmpty()) View.INVISIBLE else View.VISIBLE

    val buttonSize = 100
    val buttonMargin = 10f
    buttons.forEach { button ->
        val llayout = LinearLayout.LayoutParams(buttonSize, buttonSize)
        llayout.gravity = Gravity.CENTER
        container.addView(button, llayout)
    }
    val contWidth = (buttons.size.toFloat() + buttonMargin) * buttons.count()
    this.positionButtonContainer(container, contWidth, buttonMargin)
}

@ExperimentalUnsignedTypes
private fun AgoraVideoViewer.positionButtonContainer(container: ButtonContainer, contWidth: Float, buttonMargin: Float) {
    // TODO: Set container position and size

    container.setBackgroundColor(this.agoraSettings.colors.buttonBackgroundColor)
    container.background.alpha = this.agoraSettings.colors.buttonBackgroundAlpha
//    (container.subBtnContainer.layoutParams as? FrameLayout.LayoutParams)!!.width = contWidth.toInt()
    (this.backgroundVideoHolder.layoutParams as? ViewGroup.MarginLayoutParams)
        ?.bottomMargin = if (container.visibility == View.VISIBLE) container.measuredHeight else 0
//    this.addView(container)
}
