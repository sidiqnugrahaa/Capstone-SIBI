package com.sidiq.sibi.ui

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.sidiq.sibi.R
import com.sidiq.sibi.data.UserRepository
import com.sidiq.sibi.domain.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain

@HiltViewModel
class FirebaseAuthViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private lateinit var googleSingInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1

    private val _toast = MutableLiveData<String?>()
    val toast : LiveData<String?> get() = _toast

    private val _spinner = MutableLiveData(false)
    val spinner : LiveData<Boolean> get() = _spinner

    private val _currentUser = MutableLiveData(AuthUser())
    val currentUser : LiveData<AuthUser> get() = _currentUser

// ------------------------------------------------------------------

    fun checkUserLoggedIn(): FirebaseUser? {
        var firebaseUser: FirebaseUser? = null
        viewModelScope.launch {
            firebaseUser = repository.checkUserAuth()
        }
        return firebaseUser
    }

    fun logout() = viewModelScope.launch { repository.logout() }

    fun signInWithGoogle(activity: Activity) {
        Log.d("VIEWMDL", "CLICKED")
        launchDataLoad {
            val googleSignInOptions: GoogleSignInOptions = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            Log.d("VIEWMDL", "LAUNCHED")

            googleSingInClient = GoogleSignIn.getClient(activity, googleSignInOptions)

            val intent = googleSingInClient.signInIntent
            activity.startActivityForResult(intent, RC_SIGN_IN)

//            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
//                if(result.resultCode == RESULT_OK){
//                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                    handleSignInResult(task, activity)
//                }
//            }.launch(intent)

        }
    }

    private fun startMainActivity(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)

        activity.startActivity(intent)
        activity.finish()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, activity: Activity) {
        if(requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task, activity)

        }
    }

    private fun handleSignInResult (completedTask: Task<GoogleSignInAccount>, activity: Activity) {
        viewModelScope.launch {
            try {
                val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
                account?.let {
                    val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
                    when(val result = repository.signInWithCredential(credential)) {
                        is Result.Success -> {
                            Log.d(TAG, "Result.Success - ${result.data?.user?.uid}")
                            result.data?.user?.let {user ->
                                repository.createUser(user.toDomain()!!)
                            }
                            startMainActivity(activity)
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Result.Error - ${result.exception.message}")
                            _toast.value = result.exception.message
                        }
                        is Result.Canceled -> {
                            Log.d(TAG, "Result.Canceled")
                            _toast.value = "Request Canceled"
                        }
                    }
                }
            }
            catch (exception: Exception) {
                Toast.makeText(activity.applicationContext, "Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onToastShown(){ _toast.value = null }

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try { _spinner.value = true; block() }
            catch (e: Throwable){ _toast.value = e.message }
            finally { _spinner.value = false }
        }
    }

    companion object {
        const val TAG = "FirebaseAuthViewModel"
    }
}