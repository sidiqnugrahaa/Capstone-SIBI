package com.sidiq.sibi.data

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.sidiq.sibi.data.source.FirebaseService
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.AuthUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val service: FirebaseService
) {

    suspend fun createUser(user: AuthUser) = service.createUser(user)

    fun checkUserAuth() : AuthUser? = service.checkUserAuth()

    suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?> =
        service.signInWithCredential(authCredential)

    fun logout() = service.logout()

}