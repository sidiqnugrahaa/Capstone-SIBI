package com.sidiq.sibi.data.source



import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.utils.COLLECTION_USER
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth) {

    suspend fun createUser(user: AuthUser) : Result<Void?> {
        return try {
            val score = firestore.collection(COLLECTION_USER)
                .document(user.userId).get().await().getLong("score")
            user.score = score ?: 0
            Result.Success(
                firestore.collection(COLLECTION_USER)
                    .document(user.userId)
                    .set(user).await())
        }catch (e: Exception){
            Result.Error(e)
        }
    }

    fun checkUserAuth(): AuthUser? = auth.currentUser?.toDomain()

    fun logout() = auth.signOut()

    suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?> {
        return try{
            val result = auth.signInWithCredential(authCredential).await()
            Result.Success(result)
        }catch (e: Exception){
            Result.Error(e)
        }
    }


}