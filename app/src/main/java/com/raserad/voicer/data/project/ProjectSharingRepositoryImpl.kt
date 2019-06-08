package com.raserad.voicer.data.project

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.raserad.voicer.App
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.share.ProjectSharingRepository
import java.io.File

class ProjectSharingRepositoryImpl: ProjectSharingRepository {

    override fun share(project: Project) {

        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "video/mp4"
        intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(App.getContext()!!, "com.raserad.voicer.provider", File(project.video)))

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, project.title)
        intentShareFile.putExtra(Intent.EXTRA_TEXT, project.description)

        val sharingIntent = Intent.createChooser(intentShareFile, "Поделиться видео")
        sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(App.getContext()!!, sharingIntent, Bundle())
    }
}