package com.raserad.voicer.presentation.ui.select

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.Uri.fromParts
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.raserad.videoutils.RaseradVideoTrimmer
import com.raserad.voicer.App
import com.raserad.voicer.R
import com.raserad.voicer.di.AppDI
import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.presentation.mvp.select.VideoSelectPresenter
import com.raserad.voicer.presentation.mvp.select.VideoSelectView
import com.raserad.voicer.presentation.ui.select.entities.VideoPreviewViewData
import kotlinx.android.synthetic.main.dialog_need_files_permission.view.cancelButton
import kotlinx.android.synthetic.main.dialog_need_files_permission.view.okButton
import kotlinx.android.synthetic.main.fragment_video_select.*

class VideoSelectFragment: MvpAppCompatFragment(), VideoSelectView {

    private val videoListAdapter = VideoListAdapter()

    @InjectPresenter
    lateinit var presenter: VideoSelectPresenter

    @ProvidePresenter
    fun providePresenter(): VideoSelectPresenter = AppDI.getPresenterDI()
        .getVideoSelect(AppDI.getRouter())


    override fun onSaveInstanceState(outState: Bundle) {
        val trimData = videoTrimmer.videoTrimData
        val time = videoTrimmer.currentTime()
        val isPlaying = videoTrimmer.isPlaying()

        outState.putString("path", trimData.videoPath)
        outState.putLong("time", time)
        outState.putLong("start", trimData.startTime)
        outState.putLong("end", trimData.endTime)
        outState.putBoolean("playing", isPlaying)

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null) {
            val path = savedInstanceState.getString("path", "")
            val time = savedInstanceState.getLong("time", 0)
            val start = savedInstanceState.getLong("start", 0)
            val end = savedInstanceState.getLong("end", 0)
            val playing = savedInstanceState.getBoolean("playing", false)
            videoTrimmer.setTrimState(path, time, start, end, playing)
        }

        cancelCreateButton.setOnClickListener {
            presenter.cancelCreating()
        }

        videoListAdapter.videoSelectListener = {index ->
            presenter.selectVideo(index)
        }

        createButton.setOnClickListener {
            val trimData = videoTrimmer?.videoTrimData ?: return@setOnClickListener
            presenter.rememberTrimData(trimData.videoPath, trimData.startTime, trimData.endTime)
            presenter.showCreate()
        }

        videoListView.adapter = videoListAdapter
        videoListView.layoutManager = GridLayoutManager(activity!!, 3)
    }

    override fun checkVideoListPermissions() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), VIDEO_LIST_TAKE_PERMISSION)
            return
        }

        presenter.getVideoList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            VIDEO_LIST_TAKE_PERMISSION -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                presenter.getVideoList()
            } else {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionDescriptionDialog()
                    return
                }
                presenter.cancelCreating()
            }
        }
    }

    private fun showPermissionDescriptionDialog() {
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_need_files_permission, null, false)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .show()

        dialogView.okButton.setOnClickListener {
            val intent = Intent()
            intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = fromParts("package", App.getContext()!!.packageName, null)
            intent.data = uri
            activity?.startActivity(intent)
            presenter.cancelCreating()
            dialog.dismiss()
        }

        dialogView.cancelButton.setOnClickListener {
            presenter.cancelCreating()
            dialog.dismiss()
        }
    }

    override fun showVideoList(list: List<Video>) {
        val videoPreviewList: MutableList<VideoPreviewViewData> = ArrayList()
        list.forEach {video ->
            videoPreviewList.add(
                VideoPreviewViewData(
                    video.path,
                    false,
                    null
                )
            )
        }
        videoListAdapter.list = videoPreviewList
        videoListAdapter.notifyDataSetChanged()
    }

    override fun showEmpty(isShow: Boolean) {
        emptyVideoView.visibility = if(isShow) View.VISIBLE else View.GONE
        videoTrimmer.visibility = if(isShow) View.GONE else View.VISIBLE
        createButton.visibility = if(isShow) View.GONE else View.VISIBLE
    }


    override fun showSelectedVideo(index: Int) {
        val video = videoListAdapter.list[index]
        videoTrimmer.visibility = View.VISIBLE

        val uri = Uri.parse(video.path)
        videoTrimmer.setVideoURI(uri)

        videoListAdapter.selectVideo(index)
    }

    companion object {
        private const val VIDEO_LIST_TAKE_PERMISSION = 100
    }
}