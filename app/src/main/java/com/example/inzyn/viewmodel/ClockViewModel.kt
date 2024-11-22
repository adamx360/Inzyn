package com.example.inzyn.viewmodel


import android.os.Bundle
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClockViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null
    private val remainingTime = MutableLiveData<String>()
    private val isRunning = MutableLiveData<Boolean>()

    init {
        this.loadTimer()

    }
    fun loadTimer() {
        viewModelScope.launch(Dispatchers.IO) {
            isRunning.value = false
            remainingTime.value = "00:00"
        }
    }

    fun startTimer(minutes: Int) {
        if (minutes > 0) {
            val totalMilliseconds = minutes * 60 * 1000L
            isRunning.value = true

            countDownTimer = object : CountDownTimer(totalMilliseconds, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val remainingMinutes = (millisUntilFinished / 1000) / 60
                    val remainingSeconds = (millisUntilFinished / 1000) % 60
                    remainingTime.value =
                        String.format("%02d:%02d", remainingMinutes, remainingSeconds)
                }

                override fun onFinish() {
                    remainingTime.value = "00:00"
                    isRunning.value = false
                }
            }
            countDownTimer?.start()

        }
    }

    fun onDestinationChange(controller: NavController, destination: NavDestination, arguments: Bundle?){
        if (destination.id == R.id.clockFragment){
            this.loadTimer()
        }
    }
}