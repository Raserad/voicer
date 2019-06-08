package com.raserad.voicer.data.video

import android.provider.MediaStore
import com.raserad.voicer.App
import com.raserad.voicer.domain.video.VideoRepository
import com.raserad.voicer.domain.video.entities.Video
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VideoRepositoryImpl: VideoRepository {

    override fun getList(): Observable<List<Video>> {
        return Observable.create<List<Video>> {observer ->
                val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.Video.Media.DATA
                )
                val cursor = App.getContext()!!.contentResolver.query(uri, projection, null, null, null)

                if(cursor != null) {
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

                    val videos: MutableList<Video> = ArrayList()
                    while (cursor.moveToNext()) {
                        val path = cursor.getString(dataColumn)
                        videos.add(Video(path))
                    }

                    cursor.close()

                    observer.onNext(videos)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}