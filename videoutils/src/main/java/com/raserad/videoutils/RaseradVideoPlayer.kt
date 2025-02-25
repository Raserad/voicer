package com.raserad.videoutils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.*
import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.raserad.videoutils.interfaces.OnProgressVideoListener
import com.raserad.videoutils.interfaces.OnRangeSeekBarListener
import com.raserad.videoutils.view.ProgressBarView
import com.raserad.videoutils.view.RangeSeekBarView
import com.raserad.videoutils.view.TimeLineView
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import com.raserad.videotrimming.R


class RaseradVideoPlayer @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), SeekBar.OnSeekBarChangeListener, OnRangeSeekBarListener, OnProgressVideoListener {

    private var mHolderTopView: SeekBar? = null
    private var mRangeSeekBarView: RangeSeekBarView? = null
    private var mLinearVideo: RelativeLayout? = null
    private var mVideoView: VideoView? = null
    private var mPlayView: ImageView? = null
    private var mTextTime: TextView? = null
    private var mTimeLineView: TimeLineView? = null

    private var mSrc: Uri? = null
    private var mFinalPath: String? = null

    private val mMaxDuration = 100000000
    private var mListeners: MutableList<OnProgressVideoListener>? = null

    private var mDuration = 0L
    private val maxFileSize = 25
    private var mTimeVideo = 0L
    private var mStartPosition = 0L
    private var mEndPosition = 0L
    private var mOriginSizeFile: Long = 0
    private var mResetSeekBar = false
    private val mMessageHandler = MessageHandler(this)
    private var letUserProceed: Boolean = false
    private var mGestureDetector: GestureDetector? = null
    private var initialLength = 0L

    private var mTotalTime = 0

    private val mGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (mVideoView!!.isPlaying) {
                pause()
            } else {
                play(false)
            }
            return true
        }
    }

    var videoFinishListener: (() -> Unit)? = null

    fun currentTime(): Long {
        return mVideoView!!.currentPosition
    }

    fun isPlaying(): Boolean {
        return mVideoView!!.isPlaying
    }

    fun totalTime(): Long {
        if(mTotalTime == 0) {
            val mp = MediaPlayer.create(context, mSrc)
            mTotalTime = mp.duration
            mp.release()
        }
        return mTotalTime.toLong()
    }

    private val mTouchListener = OnTouchListener { _, event ->
        mGestureDetector!!.onTouchEvent(event)
        true
    }

    private var isPlayingVideo = false

    private val fileSize: Long
        get() {
            val file = File(mSrc!!.path)
            mOriginSizeFile = file.length()
            val fileSizeInKB = mOriginSizeFile / 1024

            return fileSizeInKB / 1024
        }

    private val croppedFileSize: Long
        get() {
            val initSize = fileSize
            val newSize: Long
            val length = (if (initialLength > 0) initialLength else 1).toLong()
            newSize = initSize / length * (mEndPosition - mStartPosition)
            return newSize / 1024
        }

    init {
        init(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(context: Context) {

        LayoutInflater.from(context).inflate(R.layout.view_video_player, this, true)

        mHolderTopView = findViewById(R.id.handlerTop)
        val progressVideoView = findViewById<ProgressBarView>(R.id.timeVideoView)
        mRangeSeekBarView = findViewById(R.id.timeLineBar)
        mLinearVideo = findViewById(R.id.layout_surface_view)
        mVideoView = findViewById(R.id.video_loader)
        mPlayView = findViewById(R.id.icon_video_play)
        mTextTime = findViewById(R.id.textTime)
        mTimeLineView = findViewById(R.id.timeLineView)

        mListeners = ArrayList()
        mListeners!!.add(this)
        mListeners!!.add(progressVideoView)

        mHolderTopView!!.max = 1000
        mHolderTopView!!.secondaryProgress = 0

        mRangeSeekBarView!!.addOnRangeSeekBarListener(this)
        mRangeSeekBarView!!.addOnRangeSeekBarListener(progressVideoView)

        val marge = mRangeSeekBarView!!.thumbs!![0].widthBitmap
        val widthSeek = mHolderTopView!!.thumb.minimumWidth / 2

        var lp = mHolderTopView!!.layoutParams as RelativeLayout.LayoutParams
        lp.setMargins(marge - widthSeek, 0, marge - widthSeek, 0)
        mHolderTopView!!.layoutParams = lp

        lp = mTimeLineView!!.layoutParams as RelativeLayout.LayoutParams
        lp.setMargins(marge, 0, marge, 0)
        mTimeLineView!!.layoutParams = lp

        lp = progressVideoView.layoutParams as RelativeLayout.LayoutParams
        lp.setMargins(marge, 0, marge, 0)
        progressVideoView.layoutParams = lp

        mHolderTopView!!.setOnSeekBarChangeListener(this)

        mVideoView!!.setOnCompletionListener {
            mVideoView?.restart()
            mVideoView?.seekTo(0)
            pauseVideo()
            setTimeVideo(0)
            setProgressBarPosition(0)
            videoFinishListener?.invoke()
        }
        mVideoView!!.setOnErrorListener {
            return@setOnErrorListener false
        }

        mHolderTopView!!.setPadding(0, 0, 0, 0)

        mGestureDetector = GestureDetector(getContext(), mGestureListener)
        mVideoView!!.setOnTouchListener(mTouchListener)

        setDefaultDestinationPath()
    }

    private fun preparePlayer() {
        val videoWidth = mVideoView!!.width
        val videoHeight = mVideoView!!.height
        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenWidth = mLinearVideo!!.width
        val screenHeight = mLinearVideo!!.height
        val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
        val lp = mVideoView!!.layoutParams

        if (videoProportion > screenProportion) {
            lp.width = screenWidth
            lp.height = (screenWidth.toFloat() / videoProportion).toInt()
        } else {
            lp.width = (videoProportion * screenHeight.toFloat()).toInt()
            lp.height = screenHeight
        }
        mVideoView!!.layoutParams = lp

        mPlayView!!.visibility = View.VISIBLE

        mDuration = mVideoView!!.duration
        setSeekBarPosition()
        setTimeVideo(0)
    }

    fun setVideoURI(videoURI: Uri) {
        val isTimeLine = mSrc == null
        mSrc = videoURI

        mVideoView!!.setVideoURI(mSrc)
        mVideoView!!.requestFocus()

        if(isTimeLine) {
            mTimeLineView!!.setVideo(mSrc!!)
        }

        mVideoView?.setOnPreparedListener {
            preparePlayer()
        }
    }

    private fun setDefaultDestinationPath() {
        val folder = Environment.getExternalStorageDirectory()
        mFinalPath = folder.path + File.separator
        Log.d(TAG, "Setting default path " + mFinalPath!!)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        var duration = mDuration * progress / 1000L

        if (fromUser) {
            if (duration < mStartPosition) {
                setProgressBarPosition(mStartPosition)
                duration = mStartPosition
            } else if (duration > mEndPosition) {
                setProgressBarPosition(mEndPosition)
                duration = mEndPosition
            }
            setTimeVideo(duration)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        isPlayingVideo = mVideoView!!.isPlaying
        pauseVideo()
        updateProgress(false)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS)

        val duration = (mDuration * seekBar.progress / 1000L)
        mVideoView!!.seekTo(duration)
        setTimeVideo(duration)
        if (isPlayingVideo) {
            mPlayView!!.visibility = View.GONE
            mVideoView!!.start()
        }
        updateProgress(false)
    }

    private fun setSeekBarPosition() {

        if (mDuration >= mMaxDuration) {
            mStartPosition = mDuration / 2 - mMaxDuration / 2
            mEndPosition = mDuration / 2 + mMaxDuration / 2

            mRangeSeekBarView!!.setThumbValue(0, (mStartPosition * 100 / mDuration).toFloat())
            mRangeSeekBarView!!.setThumbValue(1, (mEndPosition * 100 / mDuration).toFloat())

        } else {
            mStartPosition = 0
            mEndPosition = mDuration
        }

        setProgressBarPosition(mStartPosition)
        mVideoView!!.seekTo(mStartPosition)

        mTimeVideo = mDuration
        mRangeSeekBarView!!.initMaxWidth()

        initialLength = (mEndPosition - mStartPosition) / 1000
    }

    fun setPlayingState(path: String, time: Long, isPlaying: Boolean) {
        setVideoURI(Uri.parse(path))
        mVideoView!!.setOnPreparedListener {
            preparePlayer()
            mVideoView?.seekTo(time)
            setProgressBarPosition(time)
            if(isPlaying) {
                playVideo()
            }
            else {
                pauseVideo()
            }
            mVideoView?.setOnPreparedListener {}
        }

    }
    private fun pauseVideo() {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        mPlayView!!.visibility = View.VISIBLE
        mVideoView!!.pause()
    }

    fun getPath(): String {
        return mSrc!!.path!!
    }

    private fun playVideo() {
        mPlayView!!.visibility = View.GONE

        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS)
        mVideoView!!.start()
    }


    private fun setTimeVideo(position: Long) {
        mTextTime!!.text = String.format("%s", stringForTime(position))
    }

    override fun onCreate(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {

    }

    override fun onSeek(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        /*        0 is Left selector
         1 is right selector*/
        when (index) {
            0 -> {
                mStartPosition = (mDuration * value / 100L).toLong()
                mVideoView!!.seekTo(mStartPosition)
            }
            1 -> {
                mEndPosition = (mDuration * value / 100L).toLong()
            }
        }
        setProgressBarPosition(mStartPosition)

        mTimeVideo = mEndPosition - mStartPosition
        letUserProceed = croppedFileSize < maxFileSize
    }

    override fun onSeekStart(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {

    }

    override fun onSeekStop(rangeSeekBarView: RangeSeekBarView, index: Int, value: Float) {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        mVideoView!!.pause()
        mPlayView!!.visibility = View.VISIBLE
    }

    private fun stringForTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000

        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        val mFormatter = Formatter()
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private class MessageHandler internal constructor(view: RaseradVideoPlayer) : Handler() {

        private val mView: WeakReference<RaseradVideoPlayer> = WeakReference(view)

        override fun handleMessage(msg: Message) {
            val view = mView.get()
            if (view?.mVideoView == null) {
                return
            }

            view.updateProgress(true)
            if (view.mVideoView!!.isPlaying) {
                sendEmptyMessageDelayed(0, 0)
            }
        }
    }

    private fun updateProgress(all: Boolean) {
        if (mDuration == 0L) return

        val position = mVideoView!!.currentPosition
        if (all) {
            for (item in mListeners!!) {
                item.updateProgress(position, mDuration, (position * 100 / mDuration).toFloat())
            }
        } else {
            mListeners!![1].updateProgress(position, mDuration, (position * 100 / mDuration).toFloat())
        }
    }

    override fun updateProgress(time: Long, max: Long, scale: Float) {
        if (mVideoView == null) {
            return
        }

        if (time >= mEndPosition) {
            mMessageHandler.removeMessages(SHOW_PROGRESS)
            mResetSeekBar = true
            return
        }

        if (mHolderTopView != null) {
            setProgressBarPosition(time)
        }
        setTimeVideo(time)
    }


    fun play(isMuted: Boolean) {
        mVideoView!!.volume = if(isMuted) 0f else 1f
        mPlayView!!.visibility = View.GONE
        mMessageHandler.sendEmptyMessage(SHOW_PROGRESS)
        mVideoView!!.start()
    }

    fun pause() {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        mPlayView!!.visibility = View.VISIBLE
        mVideoView!!.pause()
    }

    private fun setProgressBarPosition(position: Long) {
        if (mDuration > 0) {
            val pos = 1000L * position / mDuration
            mHolderTopView!!.progress = pos.toInt()
        }
    }

    companion object {

        private val TAG = RaseradVideoTrimmer::class.java.simpleName
        private const val SHOW_PROGRESS = 2
    }
}
