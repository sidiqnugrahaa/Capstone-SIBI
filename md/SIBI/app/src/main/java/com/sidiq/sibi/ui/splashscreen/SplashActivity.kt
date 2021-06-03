package com.sidiq.sibi.ui.splashscreen

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseUser
import com.sidiq.sibi.R
import com.sidiq.sibi.SibiApp
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.MainActivity
import com.sidiq.sibi.ui.start.StartActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val viewModel : FirebaseAuthViewModel by viewModels()
    private var currentUser : FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        coroutineScope.launch {

            delay(2000)
            currentUser = viewModel.checkUserLoggedIn()

            when(currentUser) {
                null -> gotoActivity(StartActivity())
                else -> {
                    (application as SibiApp).authUser = currentUser!!.toDomain()
                    gotoActivity(MainActivity())
                }
            }

        }
    }

    private fun gotoActivity(activity: Activity) {
        val intent = Intent(this@SplashActivity, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}