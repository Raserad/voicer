package com.raserad.voicer.data.utils

import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.raserad.voicer.App

object FFmpeg {

    fun execute(command: String, onSuccess: () -> Unit, onError: (error: String?) -> Unit = {}) {
        val ffmpeg = FFmpeg.getInstance(App.getContext())

        ffmpeg.loadBinary(object: FFmpegLoadBinaryResponseHandler {
            override fun onFinish() {}

            override fun onSuccess() {
                ffmpeg.execute(command.split(" ").toTypedArray(), object : FFmpegExecuteResponseHandler {
                    override fun onFinish() {}

                    override fun onSuccess(message: String?) {
                        onSuccess()
                    }

                    override fun onFailure(message: String?) {
                        onError(message)
                    }

                    override fun onProgress(message: String?) {}

                    override fun onStart() {}

                })
            }

            override fun onFailure() {
                onError(null)
            }

            override fun onStart() {}
        })
    }
}