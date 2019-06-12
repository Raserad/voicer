package com.raserad.videoutils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
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
import com.raserad.videotrimming.R
import com.raserad.videoutils.entities.VideoTrimData
import com.raserad.videoutils.interfaces.OnProgressVideoListener
import com.raserad.videoutils.interfaces.OnRangeSeekBarListener
import com.raserad.videoutils.view.ProgressBarView
import com.raserad.videoutils.view.RangeSeekBarView
import com.raserad.videoutils.view.TimeLineView
import java.io.File
import java.lang.ref.WeakReference
import java.util.*

class RaseradVideoTrimmer @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
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

    private val mGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (mVideoView!!.isPlaying) {
                pauseVideo()
            } else {
                playVideo()
            }
            return true
        }
    }

    private val mTouchListener = OnTouchListener { v, event ->
        mGestureDetector!!.onTouchEvent(event)
        true
    }

    fun currentTime(): Long {
        return mVideoView!!.currentPosition
    }

    fun isPlaying(): Boolean {
        return mVideoView!!.isPlaying
    }

    private var isPlayingVideo = false

    val videoTrimData: VideoTrimData
        get() {
            if(mSrc != null) {
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(context, mSrc)
                val METADATA_KEY_DURATION =
                    java.lang.Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))

                val file = File(mSrc!!.path)

                if (mTimeVideo < MIN_TIME_FRAME) {

                    if (METADATA_KEY_DURATION - mEndPosition > MIN_TIME_FRAME - mTimeVideo) {
                        mEndPosition += MIN_TIME_FRAME - mTimeVideo
                    } else if (mStartPosition > MIN_TIME_FRAME - mTimeVideo) {
                        mStartPosition -= MIN_TIME_FRAME - mTimeVideo
                    }
                }

                return VideoTrimData(file.path, mStartPosition, mEndPosition)
            }
            else {
                return VideoTrimData("", 0, 0)
            }
        }

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

        LayoutInflater.from(context).inflate(R.layout.view_video_trimmer, this, true)

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
            restartVideo()
            setTimeVideo(0)
            setProgressBarPosition(0)
            mVideoView?.seekTo(0)
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
        if(mSrc != null) {
            if(mSrc!!.path == videoURI.path) {
                return
            }
        }
        mSrc = videoURI

        mVideoView!!.setVideoURI(mSrc)
        mVideoView!!.requestFocus()

        mTimeLineView!!.setVideo(mSrc!!)

        mVideoView!!.setOnPreparedListener {
            preparePlayer()
            restartVideo()
            setTimeVideo(0)
            setProgressBarPosition(0)
            mVideoView?.seekTo(0)
            mStartPosition = 0
            mEndPosition = mVideoView!!.duration
            if(mDuration > 0) {
                mRangeSeekBarView!!.setThumbValue(0, (mStartPosition * 100 / mDuration).toFloat())
                mRangeSeekBarView!!.setThumbValue(1, (mEndPosition * 100 / mDuration).toFloat())
            }
            mVideoView!!.setOnPreparedListener {  }
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

        setProgressBarPosition(mStartPosition)
        mVideoView!!.seekTo(mStartPosition)

        mTimeVideo = mDuration
        mRangeSeekBarView!!.initMaxWidth()

        initialLength = (mEndPosition - mStartPosition) / 1000
    }

    fun setTrimState(path: String, time: Long, start: Long, end: Long, isPlaying: Boolean) {
        setVideoURI(Uri.parse(path))
        mVideoView!!.setOnPreparedListener {
            preparePlayer()
            mVideoView?.seekTo(time)
            setProgressBarPosition(time)
            mStartPosition = start
            mEndPosition = end
            mRangeSeekBarView!!.setThumbValue(0, (mStartPosition * 100 / mDuration).toFloat())
            mRangeSeekBarView!!.setThumbValue(1, (mEndPosition * 100 / mDuration).toFloat())
            if(isPlaying) {
                playVideo()
            }
            else {
                pauseVideo()
            }
            mVideoView!!.setOnPreparedListener {  }
        }
    }

    private fun pauseVideo() {
        mMessageHandler.removeMessages(SHOW_PROGRESS)
        mPlayView!!.visibility = View.VISIBLE
        mVideoView!!.pause()
    }

    private fun restartVideo() {
        mVideoView?.restart()
        setProgressBarPosition(mStartPosition)
        mVideoView?.seekTo(mStartPosition)
        pauseVideo()
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

    private class MessageHandler internal constructor(view: RaseradVideoTrimmer) : Handler() {

        private val mView: WeakReference<RaseradVideoTrimmer> = WeakReference(view)

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
        if (time >= mEndPosition) {
            mMessageHandler.removeMessages(SHOW_PROGRESS)
            mResetSeekBar = true
            restartVideo()
            return
        }

        if (mHolderTopView != null) {
            setProgressBarPosition(time)
        }
        setTimeVideo(time)
    }


    private fun setProgressBarPosition(position: Long) {
        if (mDuration > 0) {
            val pos = 1000L * position / mDuration
            mHolderTopView!!.progress = pos.toInt()
        }
    }

    companion object {

        private val TAG = RaseradVideoTrimmer::class.java.simpleName
        private const val MIN_TIME_FRAME = 1000
        private const val SHOW_PROGRESS = 2
    }
}
