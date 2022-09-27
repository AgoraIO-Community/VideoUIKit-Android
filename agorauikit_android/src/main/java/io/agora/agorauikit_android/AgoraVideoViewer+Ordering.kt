package io.agora.agorauikit_android

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Shuffle around the videos depending on the style
 */
@ExperimentalUnsignedTypes
fun AgoraVideoViewer.reorganiseVideos() {
    this.organiseRecycleFloating()
    (this.backgroundVideoHolder.layoutParams as? ViewGroup.MarginLayoutParams)
        ?.topMargin =
        if (this.floatingVideoHolder.visibility == View.VISIBLE) this.floatingVideoHolder.measuredHeight else 0
    this.controlContainer?.let {
        (this.backgroundVideoHolder.layoutParams as? ViewGroup.MarginLayoutParams)
            ?.bottomMargin = if (it.visibility == View.VISIBLE) it.measuredHeight else 0
    }
    this.organiseRecycleGrid()
}

/**
 * Update the contents of the floating view
 */
@ExperimentalUnsignedTypes
fun AgoraVideoViewer.organiseRecycleFloating() {
    val gridList = this.collectionViewVideos.keys.toList()
    this.floatingVideoHolder.visibility = if (gridList.isEmpty()) View.INVISIBLE else View.VISIBLE
    if (this.floatingVideoHolder.adapter == null) {
        val remoteViewManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
        val remoteViewAdapter = FloatingViewAdapter(gridList, this)

        this.floatingVideoHolder.apply {
            layoutManager = remoteViewManager
            adapter = remoteViewAdapter
//            setHasFixedSize(true)
        }
    } else {
        (this.floatingVideoHolder.adapter as FloatingViewAdapter).uidList = gridList
        this.floatingVideoHolder.adapter?.notifyDataSetChanged()
    }
}

/**
 * Update the contents of the main grid view
 */
@ExperimentalUnsignedTypes
fun AgoraVideoViewer.organiseRecycleGrid() {
    val gridList = this.userVideosForGrid.keys.toList()
    val maxSqrt = max(1f, ceil(sqrt(gridList.count().toFloat())))

    if (this.backgroundVideoHolder.adapter == null) {
        val remoteViewManager = GridLayoutManager(
            context,
            max(maxSqrt.toInt(), 1),
            GridLayoutManager.VERTICAL,
            false
        )
        val remoteViewAdapter = GridViewAdapter(gridList, this)

        this.backgroundVideoHolder.apply {
            layoutManager = remoteViewManager
            adapter = remoteViewAdapter
//            setHasFixedSize(true)
        }
    } else {
        (this.backgroundVideoHolder.adapter as GridViewAdapter).uidList = gridList
        (this.backgroundVideoHolder.layoutManager as? GridLayoutManager)?.spanCount =
            if (gridList.count() == 2) 1 else maxSqrt.toInt()
        this.backgroundVideoHolder.adapter?.notifyDataSetChanged()
    }
}

@ExperimentalUnsignedTypes
internal class GridViewAdapter(var uidList: List<Int>, private val agoraVC: AgoraVideoViewer) :
    RecyclerView.Adapter<GridViewAdapter.RemoteViewHolder>() {
    class RemoteViewHolder(val frame: FrameLayout) : RecyclerView.ViewHolder(frame)

    val maxSqrt: Float
        get() = max(1f, ceil(sqrt(uidList.count().toFloat())))
    val mRtcEngine: RtcEngine
        get() = this.agoraVC.agkit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteViewHolder {
        val remoteFrame = FrameLayout(parent.context)

        // The width of the FrameLayout is set to half the parent's width.
        // This is to make sure that the Grid has 2 columns
        remoteFrame.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT
        )
        return RemoteViewHolder(remoteFrame)
    }

    override fun onBindViewHolder(holder: RemoteViewHolder, position: Int) {

        // First we unmute the remote video stream so that Agora can start fetching the remote video feed
        // We have to do this since we mute the remote video in the onUserJoined callback to save on bandwidth
        val uid = uidList[position]
        val videoView = agoraVC.userVideoLookup[uidList[position]]

        // We are tagging the SurfaceView object with the UID.
        // This keeps us from manually maintaining a mapping between the SurfaceView and UID
        // We'll see it used in the onViewRecycled method
        if (agoraVC.userID != uid) {
            if (agoraVC.agoraSettings.usingDualStream) {
                mRtcEngine.setRemoteVideoStreamType(
                    uidList[position],
                    if (this.itemCount < agoraVC.agoraSettings.gridThresholdHighBitrate) Constants.VIDEO_STREAM_HIGH else Constants.VIDEO_STREAM_LOW
                )
            }
//            mRtcEngine.muteRemoteVideoStream(uidList[position], false)
            // We will now use Agora's setupRemoteVideo method to render the remote video stream on the SurfaceView
//            mRtcEngine.setupRemoteVideo(videoView!!.canvas)
        } else {
//            mRtcEngine.setupLocalVideo(videoView!!.canvas)
        }
//        videoView.visibility = View.INVISIBLE

//        videoView.parent
        // We'll add the SurfaceView as a child to the FrameLayout which is actually the ViewHolder in our RecyclerView
        (videoView?.parent as? FrameLayout)?.removeView(videoView)
        holder.frame.addView(videoView)
        (holder.frame.layoutParams as? RecyclerView.LayoutParams)?.height =
            agoraVC.backgroundVideoHolder.measuredHeight / maxSqrt.toInt()
    }

    override fun onViewRecycled(holder: RemoteViewHolder) {
        // We are calling this method when our view is removed from the RecyclerView Pool.
        // This allows us to save on bandwidth

        // We get the UID from the tag of the SurfaceView
        val agoraVideoView = holder.frame.getChildAt(0) as AgoraSingleVideoView
        holder.frame.removeView(agoraVideoView)
        // We mute the remote video stream of the UID
    }

    override fun getItemCount() = uidList.size
}

@ExperimentalUnsignedTypes
internal class FloatingViewAdapter(var uidList: List<Int>, private val agoraVC: AgoraVideoViewer) :
    RecyclerView.Adapter<FloatingViewAdapter.RemoteViewHolder>() {
    class RemoteViewHolder(val frame: FrameLayout) : RecyclerView.ViewHolder(frame)

    val maxSqrt: Float
        get() = max(1f, ceil(sqrt(uidList.count().toFloat())))
    val mRtcEngine: RtcEngine
        get() = this.agoraVC.agkit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteViewHolder {
        val linearLayout = LinearLayout(parent.context)
        val pinIcon = ImageView(parent.context)
        pinIcon.setImageResource(R.drawable.baseline_push_pin_20)
        pinIcon.layoutParams = ViewGroup.LayoutParams(100, 100)
        linearLayout.addView(pinIcon)
        linearLayout.gravity = Gravity.CENTER

        val remoteFrame = FrameLayout(parent.context)
        // The width of the FrameLayout is set to half the parent's width.
        // This is to make sure that the Grid has 2 columns
        val recycleParams = RecyclerView.LayoutParams(190, 190)
        recycleParams.setMargins(5, 5, 5, 5)
        remoteFrame.layoutParams = recycleParams
        remoteFrame.setBackgroundColor(Color.BLUE)
        remoteFrame.addView(linearLayout)
        return RemoteViewHolder(remoteFrame)
    }

    override fun onBindViewHolder(holder: RemoteViewHolder, position: Int) {

        // First we unmute the remote video stream so that Agora can start fetching the remote video feed
        // We have to do this since we mute the remote video in the onUserJoined callback to save on bandwidth
        val uid = uidList[position]
        val videoView = agoraVC.userVideoLookup[uidList[position]]
        val audioMuted = agoraVC.userVideoLookup[uidList[position]]?.audioMuted
        val videoMuted = agoraVC.userVideoLookup[uidList[position]]?.videoMuted
        val activeSpeaker =
            this.agoraVC.overrideActiveSpeaker ?: this.agoraVC.activeSpeaker ?: this.agoraVC.userID
        if (activeSpeaker == uid) {
            return
        }
        // CreateRendererView is used to create a SurfaceView object
//        val surface = RtcEngine.CreateRendererView(holder.itemView.context)

        // We are tagging the SurfaceView object with the UID.
        // This keeps us from manually maintaining a mapping between the SurfaceView and UID
        // We'll see it used in the onViewRecycled method
//        videoView.tag = uidList[position]

//        videoView.parent
        // We'll add the SurfaceView as a child to the FrameLayout which is actually the ViewHolder in our RecyclerView
        (videoView?.parent as? FrameLayout)?.removeView(videoView)
        holder.frame.addView(videoView)
        if (agoraVC.userID != uid) {
            if (agoraVC.agoraSettings.usingDualStream) {
                mRtcEngine.setRemoteVideoStreamType(uidList[position], Constants.VIDEO_STREAM_LOW)
            }
            mRtcEngine.muteRemoteVideoStream(uidList[position], false)
            // We will now use Agora's setupRemoteVideo method to render the remote video stream on the SurfaceView
            mRtcEngine.setupRemoteVideo(videoView!!.canvas)
        } else {
            mRtcEngine.setupLocalVideo(videoView!!.canvas)
        }

        holder.itemView.setOnClickListener {
            val newID = if (videoView.uid == 0) this.agoraVC.userID else videoView.uid
            if (this.agoraVC.overrideActiveSpeaker == newID) {
                this.agoraVC.overrideActiveSpeaker = null
            } else {
                this.agoraVC.overrideActiveSpeaker = newID
            }
        }

//        (holder.frame.layoutParams as RecyclerView.LayoutParams).height = agoraVC.measuredHeight / maxSqrt.toInt()
    }

    override fun onViewRecycled(holder: RemoteViewHolder) {
        // We are calling this method when our view is removed from the RecyclerView Pool.
        // This allows us to save on bandwidth
        // We get the UID from the tag of the SurfaceVi ew
        (holder.frame.getChildAt(0) as? AgoraSingleVideoView)?.let {
            holder.frame.removeView(it)
        }
//        (agoraVideoView.layoutParams as FrameLayout.LayoutParams).width =
        // We mute the remote video stream of the UID
//        mRtcEngine.muteRemoteVideoStream(uid, false)
    }

    override fun getItemCount() = uidList.size
}
