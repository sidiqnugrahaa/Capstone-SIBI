package com.sidiq.sibi.domain.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    @DocumentId
    var docId: String = "",
    var timestamp: Timestamp? = null,
    var score: Long? = null
): Parcelable {

    companion object {
        private const val TAG = "user"

        fun DocumentSnapshot.toHistory(): History? = try {
            toObject(History::class.java)
        }catch (e : Exception){
            Log.e(TAG, "Error converting history", e)
            FirebaseCrashlytics.getInstance().apply {
                log("Error converting history profile")
                setCustomKey("docId", id)
                recordException(e)
            }
            null
        }

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