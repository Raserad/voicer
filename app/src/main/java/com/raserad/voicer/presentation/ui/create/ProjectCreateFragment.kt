package com.raserad.voicer.presentation.ui.create

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.raserad.voicer.presentation.mvp.create.ProjectCreatePresenter
import com.raserad.voicer.presentation.mvp.create.ProjectCreateView
import kotlinx.android.synthetic.main.fragment_project_create.*
import android.net.Uri.fromParts
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.raserad.videoutils.RaseradVideoTrimmer
import com.raserad.voicer.App
import com.raserad.voicer.R
import com.raserad.voicer.domain.video.entities.Video
import com.raserad.voicer.presentation.ui.create.entities.VideoPreviewViewData
import kotlinx.android.synthetic.main.dialog_need_files_permission.view.cancelButton
import kotlinx.android.synthetic.main.dialog_need_files_permission.view.okButton
import kotlinx.android.synthetic.main.dialog_project_create.view.*
import kotlinx.android.synthetic.main.fragment_project_create.view.*

class ProjectCreateFragment: Fragment(), ProjectCreateView {

    private val VIDEO_LIST_TAKE_PERMISSION = 100

    var presenter: ProjectCreatePresenter? = null

    private var innerView: View? = null

    private val videoListAdapter = VideoListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(innerView == null) {
            retainInstance = true
            innerView = inflater.inflate(R.layout.fragment_project_create, container, false)
        }
        return innerView
    }

    override fun onDetach() {
        super.onDetach()
        if(isRemoving) {
            presenter?.onFinish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            configure()
            getVideoList()
        }
    }

    private fun configure() {
        cancelCreateButton.setOnClickListener {
            presenter?.cancelCreating()
        }

        videoListAdapter.videoSelectListener = {index ->
            presenter?.selectVideo(index)
        }

        createButton.setOnClickListener {
            showCreateDialog()
        }

        videoListView.adapter = videoListAdapter
        videoListView.layoutManager = GridLayoutManager(activity!!, 3)
    }

    private fun showCreateDialog() {
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_project_create, null, false)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .show()

        dialogView.okButton.setOnClickListener {
            when {
                dialogView.titleView.text.isEmpty() -> dialogView.titleHintView.error = "Обязательное поле!"
                else -> {
                    presenter?.rememberProjectTitle(dialogView.titleView.text.toString())
                    presenter?.rememberProjectDescription(dialogView.descriptionView.text.toString())
                    videoTrimmer?.pauseVideo()
                    if(videoTrimmer != null) {
                        val trimData = videoTrimmer!!.videoTrimData
                        presenter?.rememberVideoTrimData(trimData.videoPath, trimData.startTime.toInt(), trimData.endTime.toInt())
                        presenter?.createProject()
                        dialog.dismiss()
                    }
                }
            }
        }

        dialogView.titleView.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialogView.titleHintView.error = null
            }

        })

        dialogView.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getVideoList() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), VIDEO_LIST_TAKE_PERMISSION)
            return
        }

        presenter?.getVideoList()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            VIDEO_LIST_TAKE_PERMISSION -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                presenter?.getVideoList()
            } else {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionDescriptionDialog()
                    return
                }
                presenter?.cancelCreating()
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
            presenter?.cancelCreating()
            dialog.dismiss()
        }

        dialogView.cancelButton.setOnClickListener {
            presenter?.cancelCreating()
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
        videoTrimmerParent.visibility = if(isShow) View.GONE else View.VISIBLE
        createButton.visibility = if(isShow) View.GONE else View.VISIBLE
    }


    private var videoTrimmer: RaseradVideoTrimmer? = null
    override fun showSelectedVideo(index: Int) {
        val video = videoListAdapter.list[index]
        val uri = Uri.parse(video.path)
        if(videoTrimmer != null) {
            (videoTrimmerParent as ViewGroup).removeView(videoTrimmer)
        }
        videoTrimmer = LayoutInflater.from(context).inflate(R.layout.view_video_trimmer, null) as RaseradVideoTrimmer
        videoTrimmerParent.visibility = View.VISIBLE
        videoTrimmerParent.addView(videoTrimmer, 0)
        videoTrimmer?.setVideoURI(uri)

        videoListAdapter.selectVideo(index)
    }

    private var progressDialog: AlertDialog? = null
    override fun showCreateProgress(isShow: Boolean) {
        createButton.visibility = if(isShow) View.GONE else View.VISIBLE
        progressDialog?.dismiss()

        if(isShow) {
            val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_project_create_progress, null, false)

            progressDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .show()
        }
    }
}