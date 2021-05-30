package com.sidiq.sibi.data.source

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.sidiq.sibi.data.wrapper.UploadResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor(private val storage: FirebaseStorage) {

    @ExperimentalCoroutinesApi
    suspend fun uploadContrib(uri: Uri) : Flow<UploadResult> = callbackFlow {
        val storageRef = storage.reference
        val photoRef = storageRef.child("contrib").child(uri.lastPathSegment!!)
        photoRef.putFile(uri)
            .addOnSuccessListener {
                it.task.continueWithTask {
                    photoRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    offer(UploadResult.Complete.Success(uri))
                    close()
                }
                close()
            }
            .addOnFailureListener {
                offer(UploadResult.Complete.Failed(it))
                close(it)
            }
            .addOnPausedListener {
                offer(UploadResult.Paused(it))
            }
            .addOnProgressListener { task ->
                val progressResult = UploadResult.InProgress(task)
                if(!offer(progressResult)) Log.e("CALLBACK", "Busy")
            }
            .addOnCanceledListener {
                offer(UploadResult.Complete.Cancelled)
                close(CancellationException("Upload Cancelled"))
            }

        awaitClose()
    }




}