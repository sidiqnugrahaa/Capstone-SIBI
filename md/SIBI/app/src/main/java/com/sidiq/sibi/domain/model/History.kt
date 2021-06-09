package com.sidiq.sibi.domain.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    @DocumentId
    var docId: String = "",
    var timestamp: Timestamp? = null,
    var type: String? = null,
    var score: Long? = 0
): Parcelable {

    companion object {
        private const val TAG = "user"

        fun QuerySnapshot.toHistory(): List<History>? = try {
            toObjects(History::class.java)
        }catch (e: Exception){
            Log.e(TAG, "Error converting history", e)
            FirebaseCrashlytics.getInstance().apply {
                log("Error converting history")
                recordException(e)
            }
            null
        }

    }


}