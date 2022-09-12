package io.agora.agorauikit_android

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import io.agora.rtc2.video.VideoCanvas

/**
 * View for the individual Agora Camera Feed.
 */
@ExperimentalUnsignedTypes
class AgoraSingleVideoView(context: Context, uid: Int, micColor: Int) : FrameLayout(context) {

    /**
     * Canvas used to render the Agora RTC Video.
     */
    var canvas: VideoCanvas
        internal set
    internal var uid: Int = uid
//    internal var textureView: AgoraTextureView = AgoraTextureView(context)

    /**
     * Is the microphone muted for this user.
     */
    var audioMuted: Boolean = true
        set(value: Boolean) {
            field = value
            (context as Activity).runOnUiThread {
                this.mutedFlag.visibility = if (value) VISIBLE else INVISIBLE
            }
        }

    /**
     * Is the video turned off for this user.
     */
    var videoMuted: Boolean = true
        set(value: Boolean) {
            if (this.videoMuted != value) {
                this.backgroundView.visibility = if (!value) INVISIBLE else VISIBLE
//                this.textureView.visibility = if (value) INVISIBLE else VISIBLE
            }
            field = value
        }

    internal val hostingView: View
        get() {
            return this.canvas.view
        }

    /**
     * Icon to show if this user is muting their microphone
     */
    var mutedFlag: ImageView
    var backgroundView: FrameLayout
    var micFlagColor: Int = micColor

    /**
     * Create a new AgoraSingleVideoView to be displayed in your app
     * @param uid: User ID of the `AgoraRtcVideoCanvas` inside this view
     * @param micColor: Color to be applied when the local or remote user mutes their microphone
     */
    init {

        val surfaceView = SurfaceView(getContext())
        this.canvas = VideoCanvas(surfaceView)
        this.canvas.uid = uid
        addView(surfaceView, ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT))
        this.backgroundView = FrameLayout(context)
        this.setBackground()
        this.mutedFlag = ImageView(context)
        this.setupMutedFlag()

        this.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
    }

    private fun setupMutedFlag() {

        val mutedLayout = FrameLayout.LayoutParams(DPToPx(context, 40), DPToPx(context, 40))
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        mutedLayout.gravity = Gravity.RIGHT
        mutedLayout.gravity = Gravity.BOTTOM
//        mutedLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        mutedLayout.bottomMargin = DPToPx(context, 5)
        mutedLayout.leftMargin = DPToPx(context, 5)

        mutedFlag.setImageResource(android.R.drawable.stat_notify_call_mute)

        mutedFlag.setColorFilter(this.micFlagColor)
        addView(mutedFlag, mutedLayout)
        this.audioMuted = true
    }

    fun setBackground() {
        backgroundView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        backgroundView.setBackgroundColor(Color.LTGRAY)
        addView(backgroundView)
        val personIcon = ImageView(context)
        personIcon.setImageResource(R.drawable.ic_person)
        val buttonLayout = FrameLayout.LayoutParams(100, 100)
        buttonLayout.gravity = Gravity.CENTER
        backgroundView.addView(personIcon, buttonLayout)
    }
}
