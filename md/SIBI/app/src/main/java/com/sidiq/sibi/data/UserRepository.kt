package com.sidiq.sibi.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.sidiq.sibi.data.source.FirebaseService
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.AuthUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: FirebaseService
) {

    fun getUsers(): Flow<Resource<List<AuthUser>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val results = service.getUsers()
                emit(results)
            }catch (e: Exception){
                emit(Resource.Failure<Nothing>(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createUser(user: AuthUser) = service.createUser(user)

    suspend fun checkUserAuth() = service.checkUserAuth()

    suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?> =
        service.signInWithCredential(authCredential)

    suspend fun logout() = service.logout()


    companion object {
        const val TAG = "UserRepository"
    }

}