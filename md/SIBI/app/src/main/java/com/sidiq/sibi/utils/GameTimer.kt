package com.sidiq.sibi.utils

import android.os.CountDownTimer

class GameTimer(
    val seconds: Long,
    var onTick: ((remainingTime: Long) -> Unit)? = null,
    var onFinish: (() -> Unit)? = null
) : CountDownTimer(seconds * 1000 + 500, 1000) {

    override fun onTick(millisUntilFinished: Long) {
        onTick?.invoke(millisUntilFinished/1000)
    }

    override fun onFinish() {
        onFinish?.invoke()
    }

}