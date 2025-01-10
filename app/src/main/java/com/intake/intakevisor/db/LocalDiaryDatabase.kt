package com.intake.intakevisor.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoodFragmentEntity::class], version = 1, exportSchema = true)
abstract class LocalDiaryDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDiaryDatabase? = null

        fun getDatabase(context: Context): LocalDiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDiaryDatabase::class.java,
                    "local_diary_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
