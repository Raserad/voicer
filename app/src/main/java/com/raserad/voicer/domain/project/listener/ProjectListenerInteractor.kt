package com.raserad.voicer.domain.project.listener

class ProjectListenerInteractor(private val projectListenerRepository: ProjectListenerRepository) {

    fun getListener() = projectListenerRepository.getListener()
}