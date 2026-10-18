package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.HistoricalMatchEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NewsHighlightEntity
import com.example.data.model.StandingEntity

@Database(
    entities = [
        MatchEntity::class,
        StandingEntity::class,
        NewsHighlightEntity::class,
        HistoricalMatchEntity::class,
        FavoriteTeamEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FutbolDatabase : RoomDatabase() {
    abstract fun futbolDao(): FutbolDao

    companion object {
        @Volatile
        private var INSTANCE: FutbolDatabase? = null

        fun getDatabase(context: Context): FutbolDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FutbolDatabase::class.java,
                    "futbol_pulse_cache.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
