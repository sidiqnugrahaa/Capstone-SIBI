package com.sidiq.sibi.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.AuthUser.Companion.toUser
import com.sidiq.sibi.domain.model.History
import com.sidiq.sibi.domain.model.History.Companion.toHistory
import com.sidiq.sibi.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardService @Inject constructor(private val firestore: FirebaseFirestore) {

    @ExperimentalCoroutinesApi
    suspend fun getGlobalRank(): Flow<Resource<List<AuthUser>>> = callbackFlow {
        val results = firestore.collection(COLLECTION_USER)
            .orderBy(FIELD_SCORE, Query.Direction.DESCENDING)
            .limit(LIMIT_RANK)

        val subscribe = results.addSnapshotListener { snapshot, _ ->
            if (snapshot == null || snapshot.isEmpty) {
                offer(Resource.Empty<Nothing>())
            }else{
                offer(Resource.Success(snapshot.toUser()!!))
            }
        }

        awaitClose { subscribe.remove() }
    }


    @ExperimentalCoroutinesApi
    suspend fun getHistory(userId: String): Flow<Resource<List<History>>> = callbackFlow {
        val results = firestore.collection(COLLECTION_USER)
            .document(userId).collection(COLLECTION_HISTORY)
            .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)

        val subscribe = results.addSnapshotListener { snapshot, _ ->
            if (snapshot == null || snapshot.isEmpty) {
                offer(Resource.Empty<Nothing>())
            }else{
                offer(Resource.Success(snapshot.toHistory()!!))
            }
        }

        awaitClose { subscribe.remove() }
    }



    @ExperimentalCoroutinesApi
    suspend fun insertHistory(userId: String, history: History): Result<Void> = try {
        val score = firestore.collection(COLLECTION_USER)
            .document(userId).get().await().getLong("score")
        val userResult = firestore.collection(COLLECTION_USER)
            .document(userId).update("score", score?.plus(history.score!!)).await()
        val result = firestore.collection(COLLECTION_USER).document(userId)
            .collection(COLLECTION_HISTORY).document().set(history).await()
        Result.Success(result)
    }catch (e: Exception){
        Result.Error(e)
    }


}