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
import com.raserad.voicer.domain.video.release.ReleaseVideoInteractor
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.SubscribeManager

@InjectViewState
class ProjectEditorPresenter(
    private val soundInteractor: SoundInteractor,
    private val soundRecordInteractor: SoundRecordInteractor,
    private val removeSoundInteractor: RemoveSoundInteractor,
    private val releaseVideoInteractor: ReleaseVideoInteractor,
    private val videoGenerateInteractor: VideoGenerateInteractor,
    private val projectSharingInteractor: ProjectSharingInteractor,
    projectBroadcastInteractor: ProjectBroadcastInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): MvpPresenter<ProjectEditorView>() {

    private val view = viewState

    private var recordList: MutableList<SoundRecord> = ArrayList()

    private var releaseVideo: ReleaseVideo? = null

    private var removedRecord: SoundRecord? = null
    private var removedRecordPosition: Int = 0

    private val project = projectBroadcastInteractor.getRemembered()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        val project = project ?: return

        val recordingListener = soundRecordInteractor.getRecordingListener()
            .doOnNext {soundRecord ->
                soundInteractor.addToProject(project, soundRecord)
                recordList.add(soundRecord)
                if(recordList.isEmpty()) {
                    view.showRecordList(recordList)
                }
                else {
                    view.showRecordInsert(0, soundRecord)
                }
                view.showRecordsEmpty(false)

                videoGenerateInteractor.generateVideo(project)

                view.showRecordingFinish()
            }

        subscribeManager.subscribe(recordingListener)

        val releaseVideoGetting = releaseVideoInteractor.getVideo(project)
            .doOnNext {video ->
                this.releaseVideo = video
                view.showVideo(video)
            }

        subscribeManager.subscribe(releaseVideoGetting)

        val recordListGetting = soundInteractor.getList(project)
            .doOnNext {list ->
                this.recordList = list
                view.showRecordList(list)
                view.showRecordsEmpty(list.isEmpty())
            }

        subscribeManager.subscribe(recordListGetting)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeSoundInteractor.removeMarked()
        subscribeManager.unsubscribeAll()
    }

    fun enableRecord(position: Int, isEnabled: Boolean) {
        val project = project ?: return

        Log.d("SHOW_ENABLED", "" + isEnabled)

        recordList[position].isEnabled = isEnabled
        soundInteractor.enableInProject(project, recordList[position], isEnabled)
        view.showRecordEnabled(position, isEnabled)
    }

    fun removeRecord(position: Int) {
        val project = project ?: return

        removedRecord = recordList.removeAt(position)
        removedRecordPosition = position

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
        removeSoundInteractor.cancelRemoving()
        view.showRecordRemoveCancelAction(false)

        recordList.add(removedRecordPosition, removedRecord!!)
        if(recordList.isEmpty()) {
            view.showRecordList(recordList)
        }
        else {
            view.showRecordInsert(removedRecordPosition, removedRecord!!)
        }
        view.showRecordsEmpty(recordList.isEmpty())

        removedRecord = null
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
}