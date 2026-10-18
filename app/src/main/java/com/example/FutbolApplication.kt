package com.example

import android.app.Application
import com.example.data.local.FutbolDatabase
import com.example.data.repository.FutbolRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FutbolApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { FutbolDatabase.getDatabase(this) }
    val repository by lazy { FutbolRepository(database.futbolDao(), applicationScope) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
