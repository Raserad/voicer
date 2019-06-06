package com.raserad.voicer.domain.project.list

class ProjectListInteractor(private val projectListRepository: ProjectListRepository) {

    fun getList() = projectListRepository.getList()

}