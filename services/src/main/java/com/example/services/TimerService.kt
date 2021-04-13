package com.example.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService : LifecycleService() {

    var isFirstRun = true
    private var isTimerEnabled = false
    private var lapTime = 0L // Time between start and pause
    private var timeRun = 0L // Total time of running
    private var timeStarted = 0L // Timestamp when the timer was started
    private var lastSecondTimestamp = 0L // Last whole second passed!

    private val timeRunInSeconds = MutableLiveData<Long>() // For notification!

    companion object {
        val timeRunInMillis = MutableLiveData<Long>() // For observing outside the service - From Activity or Fragment
        val isTracking = MutableLiveData<Boolean>()
    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        isTracking.observe(this, Observer {
            // TODO - Make some action
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when(it.action) {
                Constants.ACTION_START_OR_RESUME_SERVICE -> { // Запуск или возобновление таймера
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                        startTimer()
                    }
                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    Timber.d("Pausing service")
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stopping service")
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Will track the actual runner time and trigger changes for observable values
    // Will be called always when starting or resuming service
    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        // Track time in the coroutine
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // Разница между текущим временем и временем старта таймера
                lapTime = System.currentTimeMillis() - timeStarted
                // Post the new lapTime
                timeRunInMillis.postValue(timeRun + lapTime)
                // Update the Seconds counter model only if one second or more has passed!
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                // Update once a 50ms
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }

            // Update total running time if the service was stopped or paused!
            timeRun += lapTime
        }
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun stopService() {
        stopForeground(true)
        postInitialValues()
        isFirstRun = true
    }

    // Start the service in Foreground
    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Для AndroidAPI версии 26 и выше - используем NotificationChannel функционал
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_timer_foreground)
                .setContentTitle("Timer")
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).also {

            },
            FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}