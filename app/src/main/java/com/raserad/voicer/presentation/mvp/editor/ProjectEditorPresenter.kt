package com.raserad.voicer.presentation.mvp.editor

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.raserad.voicer.domain.project.share.ProjectSharingInteractor
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastInteractor
import com.raserad.voicer.domain.sound.SoundInteractor
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.sound.record.SoundRecordInteractor
import com.raserad.voicer.domain.sound.remove.RemoveSoundInteractor
import com.raserad.voicer.domain.video.generate.VideoGenerateInteractor
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.SubscribeManager

@InjectViewState
class ProjectEditorPresenter(
    private val soundInteractor: SoundInteractor,
    private val soundRecordInteractor: SoundRecordInteractor,
    private val removeSoundInteractor: RemoveSoundInteractor,
    private val videoGenerateInteractor: VideoGenerateInteractor,
    private val projectSharingInteractor: ProjectSharingInteractor,
    projectBroadcastInteractor: ProjectBroadcastInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): MvpPresenter<ProjectEditorView>() {

    private val view = viewState

    private var recordList: MutableList<SoundRecord> = ArrayList()

    private var removedRecord: SoundRecord? = null
    private var removedRecordPosition: Int = 0

    private val project = projectBroadcastInteractor.getRemembered()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val project = project ?: return

        val recordingListener = soundRecordInteractor.getRecordingListener()
            .flatMap {soundRecord ->
                soundInteractor.addToProject(project, soundRecord)
            }
            .doOnNext {soundRecord ->
                recordList.add(0, soundRecord)
                if(recordList.isEmpty()) {
                    view.showRecordList(recordList)
                }
                else {
                    view.showRecordInsert(0, soundRecord)
                }
                view.showRecordsEmpty(false)

                view.showRecordingFinish()
            }
            .doOnNext { generateVideo() }

        subscribeManager.subscribe(recordingListener)

        val recordListGetting = soundInteractor.getList(project)
            .doOnNext {list ->
                this.recordList = list
                view.showRecordList(list)
                view.showRecordsEmpty(list.isEmpty())
            }
        subscribeManager.subscribe(recordListGetting)

        view.showVideo(project.video)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribeManager.unsubscribeAll()

        subscribeManager.subscribe(removeSoundInteractor.removeMarked())
    }

    fun enableRecord(position: Int, isEnabled: Boolean) {
        val project = project ?: return

        val enablingRecord = soundInteractor.enableInProject(project, recordList[position], isEnabled)
            .doOnNext {
                recordList[position].isEnabled = isEnabled
                view.showRecordEnabled(position, isEnabled)

                generateVideo()
            }

        subscribeManager.subscribe(enablingRecord)
    }

    fun removeRecord(position: Int) {
        subscribeManager.unsubscribe("remove_canceling")
        val project = project ?: return

        removedRecord = recordList.removeAt(position)
        removedRecordPosition = position

        generateVideo()

        view.showRecordRemoveCancelAction(true)
        view.showRecordRemove(position)
        view.showRecordsEmpty(recordList.isEmpty())

        val recordRemoving = removeSoundInteractor.removeFromProject(project, removedRecord!!)
            .doOnNext{
                view.showRecordRemoveCancelAction(false)
            }

        subscribeManager.subscribe(recordRemoving, "record_removing")
    }

    fun cancelRecordRemoving() {
        subscribeManager.unsubscribe("record_removing")
        val removeCanceling = removeSoundInteractor.cancelRemoving()
            .doOnNext {
                view.showRecordRemoveCancelAction(false)

                recordList.add(removedRecordPosition, removedRecord!!)
                if(recordList.isEmpty()) {
                    view.showRecordList(recordList)
                }
                else {
                    view.showRecordInsert(removedRecordPosition, removedRecord!!)
                }
                view.showRecordsEmpty(recordList.isEmpty())

                generateVideo()

                removedRecord = null
            }

        subscribeManager.subscribe(removeCanceling, "remove_canceling")
    }

    fun startRecording(time: Long) {
        view.showSoundRecordMessage(true)
        view.playVideo(true)
        soundRecordInteractor.startRecording(time)
    }

    fun stopRecording(time: Long, totalTime: Long) {
        view.showSoundRecordMessage(false)
        view.playVideo(false)
        soundRecordInteractor.stopRecording(time, totalTime)
    }

    fun shareProject() {
        val project = project ?: return

        projectSharingInteractor.share(project)
    }

    fun back() {
        router.back()
    }

    private fun generateVideo() {

        val project = project ?: return

        view.showVideoGeneratingProgress(true)

        val videoGenerating = videoGenerateInteractor.generateVideo(project)
            .doOnNext {
                view.showVideoGeneratingProgress(false)
                view.showRecordingFinish()
            }

        subscribeManager.subscribe(videoGenerating, "video_generating")
    }
}