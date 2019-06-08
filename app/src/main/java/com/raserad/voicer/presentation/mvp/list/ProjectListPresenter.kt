package com.raserad.voicer.presentation.mvp.list

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.list.ProjectListInteractor
import com.raserad.voicer.domain.project.listener.ProjectEventType
import com.raserad.voicer.domain.project.listener.ProjectListenerInteractor
import com.raserad.voicer.domain.project.remove.ProjectRemoveInteractor
import com.raserad.voicer.domain.project.share.ProjectSharingInteractor
import com.raserad.voicer.domain.project.broadcast.ProjectBroadcastInteractor
import com.raserad.voicer.presentation.Router
import com.raserad.voicer.presentation.utils.SubscribeManager

@InjectViewState
class ProjectListPresenter(
    private val projectListInteractor: ProjectListInteractor,
    private val projectRemoveInteractor: ProjectRemoveInteractor,
    private val projectSharingInteractor: ProjectSharingInteractor,
    private val projectListenerInteractor: ProjectListenerInteractor,
    private val projectBroadcastInteractor: ProjectBroadcastInteractor,
    private val subscribeManager: SubscribeManager,
    private val router: Router
): MvpPresenter<ProjectListView>() {

    private val view = viewState

    private var list: MutableList<Project> = ArrayList()

    private var removedProject: Project? = null
    private var removePosition: Int = 0

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showProjectList()

        val projectListening = projectListenerInteractor.getListener()
            .doOnNext {event ->
                when(event.type) {
                    ProjectEventType.CREATE -> {
                        list.add(0, event.project)
                        if(list.count() <= 1) {
                            view.showList(list)
                        }
                        else {
                            view.showProjectInsert(0, event.project)
                        }
                        view.showEmpty(false)
                    }
                }
            }

        subscribeManager.subscribe(projectListening)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscribeManager.unsubscribeAll()
        subscribeManager.subscribe(projectRemoveInteractor.removeMarked())
    }

    private fun showProjectList() {
        val projectListGetting = projectListInteractor.getList()
            .doOnNext {list ->
                this.list = list
                view.showEmpty(list.isEmpty())
                view.showList(list)
            }

        subscribeManager.subscribe(projectListGetting, "list_getting")
    }

    fun shareProject(position: Int) {
        val project = list[position]
        projectSharingInteractor.share(project)
    }

    fun removeProject(position: Int) {
        subscribeManager.unsubscribe("remove_canceling")

        removedProject = list.removeAt(position)
        removePosition = position

        view.showRemoveCancelAction(true)

        view.showProjectRemove(position)
        view.showEmpty(list.isEmpty())

        val projectRemoving = projectRemoveInteractor.remove(removedProject!!).doOnNext {
            view.showRemoveCancelAction(false)
        }

        subscribeManager.subscribe(projectRemoving, "deleting")
    }

    fun cancelProjectRemoving() {
        subscribeManager.unsubscribe("deleting")

        val removeCanceling = projectRemoveInteractor.cancelRemoving()
            .doOnNext {
                view.showRemoveCancelAction(false)

                list.add(removePosition, removedProject!!)
                if(list.count() <= 1) {
                    view.showList(list)
                }
                else {
                    view.showProjectInsert(removePosition, removedProject!!)
                }
                view.showEmpty(list.isEmpty())

                removedProject = null
            }

        subscribeManager.subscribe(removeCanceling, "remove_canceling")
    }

    fun showProjectEditor(position: Int) {
        val project = list[position]
        projectBroadcastInteractor.remember(project)

        router.showProjectEditor()
    }

    fun showProjectCreate() {
        router.showVideSelect()
    }
}