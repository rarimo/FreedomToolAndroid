package org.freedomtool.utils

import android.os.Handler
import android.os.Looper

class TimerManager(private val callback: () -> Unit) {
    private val handler = Handler(Looper.getMainLooper())
    private var isTimerRunning = false

    fun startTimer(delayMillis: Long) {
        if (!isTimerRunning) {
            handler.postDelayed({
                callback.invoke()
                isTimerRunning = false
            }, delayMillis)
            isTimerRunning = true
        }
    }

    fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
        isTimerRunning = false
    }
}
