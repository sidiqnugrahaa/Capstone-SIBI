package com.sidiq.sibi.data.wrapper

import com.google.firebase.storage.UploadTask

sealed class UploadResult {

    data class InProgress(val task: UploadTask.TaskSnapshot) : UploadResult()
    data class Paused(val task: UploadTask.TaskSnapshot) : UploadResult()

    sealed class Complete: UploadResult(){
        object Success : Complete()
        data class Failed(val error: Throwable) : Complete()
        object Cancelled : Complete()
    }

}