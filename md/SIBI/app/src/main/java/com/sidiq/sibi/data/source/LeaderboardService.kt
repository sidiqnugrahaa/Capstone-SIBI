package com.sidiq.sibi.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.AuthUser.Companion.toUser
import com.sidiq.sibi.domain.model.History
import com.sidiq.sibi.domain.model.History.Companion.toHistory
import com.sidiq.sibi.utils.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardService @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getGlobalRank(): Resource<List<AuthUser>> {
        val results = firestore.collection(COLLECTION_USER)
            .orderBy(FIELD_SCORE, Query.Direction.DESCENDING)
            .limit(LIMIT_RANK)
            .get().await().toUser()
            ?: return Resource.Empty()
        return if(results.isEmpty()) Resource.Empty() else Resource.Success(results)
    }

    suspend fun getHistory(userId: String): Resource<List<History>> {
        val results = firestore.collection(COLLECTION_USER)
            .document(userId).collection(COLLECTION_HISTORY)
            .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
            .get().await().toHistory()
            ?: return Resource.Empty()
        return if(results.isEmpty()) Resource.Empty() else Resource.Success(results)
    }

}