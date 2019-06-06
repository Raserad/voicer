package com.raserad.voicer.data.project

import com.raserad.voicer.data.video.entites.ReleaseVideoObject
import com.raserad.voicer.domain.project.entities.Project
import com.raserad.voicer.domain.project.share.ProjectSharingRepository
import com.raserad.voicer.domain.video.release.entities.ReleaseVideo
import io.realm.Realm
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.raserad.voicer.App
import android.os.StrictMode
import androidx.core.content.FileProvider
import java.io.File

class ProjectSharingRepositoryImpl: ProjectSharingRepository {

    override fun share(project: Project) {
        val realm = Realm.getDefaultInstance()

        val video = realm.where(ReleaseVideoObject::class.java).equalTo("uid", project.uid).findFirst()

        val releaseVideo = ReleaseVideo(video!!.path)

        realm.close()

        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "video/mp4"
        intentShareFile.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(App.getContext()!!, "com.raserad.voicer.provider", File(releaseVideo.path)))

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, project.title)
        intentShareFile.putExtra(Intent.EXTRA_TEXT, project.description)

        val sharingIntent = Intent.createChooser(intentShareFile, "Поделиться видео")
        sharingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(App.getContext()!!, sharingIntent, Bundle())
    }
}