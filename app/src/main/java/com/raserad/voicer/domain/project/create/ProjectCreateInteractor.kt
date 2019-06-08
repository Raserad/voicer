package com.raserad.voicer.domain.project.create

import com.raserad.voicer.domain.project.create.entities.ProjectCreateData
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.listener.ProjectEventType
import com.raserad.voicer.domain.project.listener.ProjectListenerRepository
import com.raserad.voicer.domain.video.trim.VideoTrimRepository
import io.reactivex.Observable
import kotlin.random.Random

class ProjectCreateInteractor(
    private val projectCreateRepository: ProjectCreateRepository,
    private val projectListenerRepository: ProjectListenerRepository,
    private val videoTrimRepository: VideoTrimRepository
) {

    fun create(projectCreateData: ProjectCreateData): Observable<Project> {
        lateinit var project: Project
        return videoTrimRepository.trim(projectCreateData.videoTrimData)
            .flatMap {video ->
                project = Project(generateUid(), projectCreateData.title, projectCreateData.description, video.path)
                projectCreateRepository.create(project)
            }
            .map {
                projectListenerRepository.notify(ProjectEventType.CREATE, project)
                project
            }
            .map {
                project
            }
    }

    private fun generateUid(length: Int = 40): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}