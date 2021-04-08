package com.example.servicesandjobs.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CustomService : Service() {

    val logTag = "CustomService"
    var counter = 0

    override fun onCreate() {
        Log.d(logTag, "Service created")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(logTag, "Service command execuded")

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(logTag, "Something connected to service")
        TODO("Return the communication channel to the service.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(logTag, "Disconnected from service")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(logTag, "Service destroyed")
        super.onDestroy()
    }
}