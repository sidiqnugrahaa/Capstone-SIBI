package com.sidiq.sibi

import android.app.Application
import com.sidiq.sibi.domain.model.AuthUser
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SibiApp : Application() {

    var authUser: AuthUser? = null

}