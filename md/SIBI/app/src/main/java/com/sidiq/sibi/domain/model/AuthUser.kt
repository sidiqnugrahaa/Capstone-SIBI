package com.sidiq.sibi.domain.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthUser (
    @DocumentId
    var userId: String = "",
    var name: String = "",
    var imageUrl: String = "",
    var score: Long = 0
) : Parcelable {

    companion object {
        private const val TAG = "user"

        fun DocumentSnapshot.toUser(): AuthUser? = try {
            toObject(AuthUser::class.java)
        }catch (e : Exception){
            Log.e(TAG, "Error converting user profile", e)
            FirebaseCrashlytics.getInstance().apply {
                log("Error converting user profile")
                setCustomKey("userId", id)
                recordException(e)
            }
            null
        }

        fun FirebaseUser.toDomain(): AuthUser? = try {
            AuthUser(
                userId = email!!,
                name = displayName!!,
                imageUrl = photoUrl.toString(),
                score = 0)
        }catch (e:Exception){
            Log.e(TAG, "Error converting user profile", e)
            FirebaseCrashlytics.getInstance().apply {
                log("Error converting user profile")
                setCustomKey("userId", uid)
                recordException(e)
            }
            null
        }

        fun QuerySnapshot.toUser(): List<AuthUser>? = try {
            toObjects(AuthUser::class.java)
        }catch (e: Exception){
            Log.e(TAG, "Error converting user profile", e)
            FirebaseCrashlytics.getInstance().apply {
                log("Error converting user profile")
                recordException(e)
            }
            null
        }

    }

}