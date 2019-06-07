package com.raserad.voicer.presentation.ui.editor

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.snackbar.Snackbar
import com.raserad.voicer.App
import com.raserad.voicer.R
import com.raserad.voicer.di.AppDI
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import com.raserad.voicer.presentation.mvp.editor.ProjectEditorPresenter
import com.raserad.voicer.presentation.mvp.editor.ProjectEditorView
import com.raserad.voicer.presentation.ui.editor.entities.SoundRecordViewData
import kotlinx.android.synthetic.main.dialog_need_audio_record_permission.view.*
import kotlinx.android.synthetic.main.fragment_project_editor.*

class ProjectEditorFragment: MvpAppCompatFragment(), ProjectEditorView {

    private val SOUND_RECORD = 2

    private val recordListAdapter = RecordListAdapter()

    private var recordRemoveSnackbar: Snackbar? = null

    @InjectPresenter
    lateinit var presenter: ProjectEditorPresenter

    @ProvidePresenter
    fun providePresenter() = AppDI.getPresenterDI()
        .getProjectEditor(AppDI.getRouter())

    override fun onSaveInstanceState(outState: Bundle) {
        val path = videoPlayer.getPath()
        val time = videoPlayer.currentTime()
        val isPlaying = videoPlayer.isPlaying()

        outState.putString("path", path)
        outState.putLong("time", time)
        outState.putBoolean("playing", isPlaying)

        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null) {
            val path = savedInstanceState.getString("path", "")
            val time = savedInstanceState.getLong("time", 0)
            val playing = savedInstanceState.getBoolean("playing", false)
            videoPlayer.setPlayingState(path, time, playing)
        }

        soundRecordButton.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> recordSound()
                MotionEvent.ACTION_UP -> stopRecording()
            }

            true
        }

        videoPlayer.videoFinishListener = {
            presenter.stopRecording(videoPlayer.totalTime(), videoPlayer.totalTime())
        }

        backButton.setOnClickListener {
            presenter.back()
        }

        shareProjectButton.setOnClickListener {
            presenter.shareProject()
        }

        recordListAdapter.soundTrackListener = { position, action ->
            when(action) {
                SoundRecordActions.ENABLE -> presenter.enableRecord(position, true)
                SoundRecordActions.DISABLE -> presenter.enableRecord(position, false)
                SoundRecordActions.REMOVE -> presenter.removeRecord(position)
            }
        }

        recordsList.adapter = recordListAdapter
        recordsList.layoutManager = LinearLayoutManager(context)
    }

    private fun recordSound() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), SOUND_RECORD)
            return
        }

        presenter.startRecording(videoPlayer.currentTime())
    }

    private fun stopRecording() {
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        presenter.stopRecording(videoPlayer.currentTime(), videoPlayer.totalTime())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            SOUND_RECORD -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            } else {
                if(!shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    showPermissionDescriptionDialog()
                    return
                }
            }
        }
    }

    private fun showPermissionDescriptionDialog() {
        val dialogView = LayoutInflater.from(activity!!).inflate(R.layout.dialog_need_audio_record_permission, null, false)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .show()

        dialogView.okButton.setOnClickListener {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", App.getContext()!!.packageName, null)
            intent.data = uri
            activity?.startActivity(intent)
            dialog.dismiss()
        }

        dialogView.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun showSoundRecordMessage(isShow: Boolean) {
        soundRecordMessage.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun playVideo(isPlaying: Boolean) {
        if(isPlaying) videoPlayer.play(true) else videoPlayer.pause()
    }


    override fun showVideo(video: ReleaseVideo) {
        val videoUri = Uri.parse(video.path)
        videoPlayer.setVideoURI(videoUri)
    }

    override fun showRecordsEmpty(isShow: Boolean) {
        emptyRecordList.visibility = if(isShow) View.VISIBLE else View.GONE
    }

    override fun showRecordList(list: MutableList<SoundRecord>) {
        val preparedList: MutableList<SoundRecordViewData> = ArrayList()

        list.forEach {soundRecord ->
            val soundRecordViewData = SoundRecordViewData(soundRecord.path, soundRecord.start, soundRecord.end, soundRecord.total, soundRecord.isEnabled)
            preparedList.add(soundRecordViewData)
        }

        recordListAdapter.list = preparedList
        recordListAdapter.notifyDataSetChanged()
    }

    override fun showRecordInsert(position: Int, record: SoundRecord) {
        val soundRecordViewData = SoundRecordViewData(record.path, record.start, record.end, record.total, record.isEnabled)
        recordListAdapter.list.add(position, soundRecordViewData)
        recordListAdapter.notifyItemInserted(position)
        recordsList.scrollToPosition(position)
    }

    override fun showRecordRemove(position: Int) {
        recordListAdapter.list.removeAt(position)
        recordListAdapter.notifyItemRemoved(position)
    }

    override fun showRecordEnabled(position: Int, isEnabled: Boolean) {
        recordListAdapter.list[position].isEnabled = isEnabled
        recordListAdapter.notifyItemChanged(position)
    }

    override fun showRecordRemoveCancelAction(isShow: Boolean) {
        recordRemoveSnackbar?.dismiss()

        if(isShow && view != null) {
            recordRemoveSnackbar = Snackbar.make(view!!, R.string.sound_record_remove, Snackbar.LENGTH_INDEFINITE)
            recordRemoveSnackbar?.setAction(R.string.project_removed_cancel) {
                presenter.cancelRecordRemoving()
            }
            recordRemoveSnackbar?.show()
        }
    }

    override fun showRecordingFinish() {

    }
}