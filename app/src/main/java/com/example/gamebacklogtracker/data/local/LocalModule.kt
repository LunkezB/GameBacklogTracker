package com.example.gamebacklogtracker.data.local

import android.content.Context
import androidx.room.Room
import com.example.gamebacklogtracker.util.Constants

object LocalModule {

    @Volatile
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                Constants.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { createdDatabase ->
                    database = createdDatabase
                }
        }
    }
}