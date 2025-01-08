package com.example.inzyn.viewmodel

import android.icu.text.DecimalFormat
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClockViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private val _remainingTime = MutableLiveData<String>()
    val remainingTime: LiveData<String> get() = _remainingTime

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> get() = _isRunning

    private var timeMillis: Long = 0L
    private var starterTime: Long = 0L

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _isRunning.value = false
                _remainingTime.value = "00:00"
            }
        }
    }

    fun startTimer(interval: Long = 1000L) {
        stopTimer()
        countDownTimer = object : CountDownTimer(timeMillis, interval) {
            override fun onTick(millisUntilFinished: Long) {
                timeMillis = millisUntilFinished
                updateTimeDisplay(timeMillis)
            }

            override fun onFinish() {
                _remainingTime.postValue("00:00")
                _isRunning.postValue(true)
            }
        }.start()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
    }

    fun addTime(seconds: Long) {
        timeMillis += seconds * 1000
        updateTimeDisplay(timeMillis)
    }

    fun subtractTime(seconds: Long) {
        timeMillis = (timeMillis - seconds * 1000).coerceAtLeast(0)
        updateTimeDisplay(timeMillis)
    }

    fun resetTimer() {
        stopTimer()
        timeMillis = starterTime
        updateTimeDisplay(timeMillis)
        _isRunning.value = false
    }

    private fun updateTimeDisplay(millis: Long) {
        val f = DecimalFormat("00")
        val min = (millis / 60000) % 60
        val sec = (millis / 1000) % 60
        _remainingTime.postValue("${f.format(min)}:${f.format(sec)}")
    }

//    fun playSound(context: Context) {
//        mediaPlayer = MediaPlayer.create(context, R.raw.timer)
//        mediaPlayer.start()
//        mediaPlayer.setOnCompletionListener {
//            it.release()
//        }
//    }
}