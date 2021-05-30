package com.sidiq.sibi.data.wrapper

import android.net.Uri
import com.google.firebase.storage.UploadTask

sealed class UploadResult {

    data class InProgress(val task: UploadTask.TaskSnapshot) : UploadResult()
    data class Paused(val task: UploadTask.TaskSnapshot) : UploadResult()

    sealed class Complete: UploadResult(){
        data class Success(val downloadUri: Uri) : Complete()
        data class Failed(val error: Throwable) : Complete()
        object Cancelled : Complete()
    }

}