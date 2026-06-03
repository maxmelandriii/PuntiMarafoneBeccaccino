package com.maxmelandriii.contapuntimarafone

import android.app.Application
import androidx.room.Room
import com.maxmelandriii.contapuntimarafone.data.local.AppDatabase

class MarafoneApplication : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "marafone-db"
            ).fallbackToDestructiveMigration(false).build()
    }

    companion object {
        lateinit var instance: MarafoneApplication
            private set
    }
}
