package com.sidiq.sibi.data.source

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sidiq.sibi.data.wrapper.UploadResult
import com.sidiq.sibi.domain.model.Contrib.Companion.toHistory
import com.sidiq.sibi.domain.model.Word
import com.sidiq.sibi.utils.COLLECTION_HISTORY
import com.sidiq.sibi.utils.COLLECTION_USER
import com.sidiq.sibi.utils.COLLECTION_WORD
import com.sidiq.sibi.utils.COLLECTION_WORD_FILE
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage) {

    @ExperimentalCoroutinesApi
    suspend fun uploadContrib(word: Word) : Flow<UploadResult> = callbackFlow {
        val uri = Uri.parse(word.contrib?.fileUri)
        val storageRef = storage.reference
        val photoRef = storageRef.child("contrib").child(word.word).child(uri.lastPathSegment!!)
        photoRef.putFile(uri)
            .addOnSuccessListener {
                it.task.continueWithTask {
                    photoRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    word.contrib!!.fileUri = uri.toString()
                    insertWord(word).addOnSuccessListener {
                        insertHistory(word).addOnSuccessListener {
                            offer(UploadResult.Complete.Success)
                            close()
                        }
                    }
                }
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


    private fun insertWord(word: Word) =
        firestore.collection(COLLECTION_WORD).document(word.word)
            .collection(COLLECTION_WORD_FILE).document().set(word.contrib!!)

    private fun insertHistory(word: Word) =
        firestore.collection(COLLECTION_USER).document(word.contrib!!.userId)
            .collection(COLLECTION_HISTORY).document().set(word.contrib!!.toHistory())




}