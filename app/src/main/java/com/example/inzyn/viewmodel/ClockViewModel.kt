package com.example.inzyn.viewmodel


import android.content.Context
import android.icu.text.DecimalFormat
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inzyn.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ClockViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private val _remainingTime = MutableLiveData<String>()
    val remainingTime: LiveData<String> get() = _remainingTime

    private val _isRunning = MutableLiveData<Boolean>()
    val isRunning: LiveData<Boolean> get() = _isRunning

    private var time: Long = 0L
    private var starterTime: Long = 0L
    private lateinit var mediaPlayer: MediaPlayer

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _isRunning.postValue(false)
                _remainingTime.postValue("00:00")
            }
        }
    }

    fun startTimer(interval: Long = 1000L) {
        stopTimer()
        countDownTimer = object : CountDownTimer(time, interval) {
            override fun onTick(millisUntilFinished: Long) {
                time = millisUntilFinished
                updateTimeDisplay(time)
            }

            override fun onFinish() {
                _remainingTime.postValue("00:00")
                _isRunning.postValue(true)
            }
        }.start()
    }

    fun updateTimeDisplay(millis: Long) {
        val f = DecimalFormat("00")
        val min = (millis / 60000) % 60
        val sec = (millis / 1000) % 60
        _remainingTime.postValue("${f.format(min)}:${f.format(sec)}")
    }

    fun stopTimer() {
        countDownTimer?.cancel()
    }

    fun addTime(seconds: Long) {
        time += seconds * 1000
        updateTimeDisplay(time)
    }

    fun subtractTime(seconds: Long) {
        time = (time - seconds * 1000).coerceAtLeast(0)
        updateTimeDisplay(time)
    }

    fun resetTimer() {
        stopTimer()
        time = starterTime
        updateTimeDisplay(time)
        _isRunning.postValue(false)
    }

    fun playSound(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.timer)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }

}