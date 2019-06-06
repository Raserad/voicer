package com.raserad.voicer.presentation.mvp.editor

import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.share.ProjectSharingInteractor
import com.raserad.voicer.domain.sound.SoundInteractor
import com.raserad.voicer.domain.sound.record.SoundRecordInteractor
import com.raserad.voicer.domain.sound.entities.SoundRecord
import com.raserad.voicer.domain.sound.remove.RemoveSoundInteractor
import com.raserad.voicer.domain.video.generate.VideoGenerateInteractor
import com.raserad.voicer.domain.video.release.ReleaseVideoInteractor
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.mvp.Presenter
import com.raserad.voicer.presentation.utils.SubscribeManager

class ProjectEditorPresenter(
    private val view: ProjectEditorView,
    private val soundInteractor: SoundInteractor,
    private val soundRecordInteractor: SoundRecordInteractor,
    private val removeSoundInteractor: RemoveSoundInteractor,
    private val releaseVideoInteractor: ReleaseVideoInteractor,
    private val videoGenerateInteractor: VideoGenerateInteractor,
    private val projectSharingInteractor: ProjectSharingInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router,
    private val project: Project
): Presenter {

    private var recordList: MutableList<SoundRecord> = ArrayList()

    private var releaseVideo: ReleaseVideo? = null
    private var removedRecord: SoundRecord? = null
    private var removedRecordPosition: Int = 0

    override fun onStart() {
        val recordingListener = soundRecordInteractor.getRecordingListener()
            .doOnNext {soundRecord ->
                soundInteractor.addToProject(project, soundRecord)
                recordList.add(soundRecord)
                view.showRecordInsert(0, soundRecord)
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

    override fun onFinish() {
        removeSoundInteractor.removeMarked()
        subscribeManager.unsubscribeAll()
    }

    fun enableRecord(position: Int, isEnabled: Boolean) {
        recordList[position].isEnabled = isEnabled
        soundInteractor.enableInProject(project, recordList[position], isEnabled)
        view.showRecordEnabled(position, isEnabled)
    }

    fun removeRecord(position: Int) {
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
        view.showRecordRemoveCancelAction(false)
        removeSoundInteractor.cancelRemoving()

        recordList.add(removedRecordPosition, removedRecord!!)
        view.showRecordInsert(removedRecordPosition, removedRecord!!)
        view.showRecordsEmpty(recordList.isEmpty())

        removedRecord = null
    }

    fun startRecording(time: Long) {
        view.showSoundRecordMessage(true)
        view.playVideo(true)
        soundRecordInteractor.startRecording(time)
    }

    fun stopRecording(time: Long) {
        view.showSoundRecordMessage(false)
        view.playVideo(false)
        soundRecordInteractor.stopRecording(time)
    }

    fun shareProject() {
        projectSharingInteractor.share(project)
    }

    fun back() {
        router.back()
    }
}