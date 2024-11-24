package com.shub39.grit.habits.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [HabitEntity::class, HabitStatusEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitStatusDao(): HabitStatusDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE habit_index ADD COLUMN title TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE habit_index SET title = id")
                db.execSQL(
                    """
                    CREATE TABLE habit_index_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        time INTEGER NOT NULL
                    )
                    """
                )
                db.execSQL("INSERT INTO habit_index_new (title, description, time) SELECT title, description, time FROM habit_index")
                db.execSQL("DROP TABLE habit_index")
                db.execSQL("ALTER TABLE habit_index_new RENAME TO habit_index")

                // Habits Status table
                db.execSQL("ALTER TABLE habit_status ADD COLUMN habitId_new INTEGER")
                db.execSQL(
                    """
                    UPDATE habit_status
                    SET habitId_new = (
                        SELECT id FROM habit_index WHERE habit_index.title = habit_status.habitId
                    )
                    """
                )
                db.execSQL(
                    """
                    CREATE TABLE habit_status_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        habitId INTEGER NOT NULL,
                        date INTEGER NOT NULL,  -- Use INTEGER for date as timestamp
                        FOREIGN KEY(habitId) REFERENCES habit_index(id) ON DELETE CASCADE
                    )
                    """
                )
                db.execSQL("INSERT INTO habit_status_new (id, habitId, date) SELECT id, habitId_new, strftime('%s', date) FROM habit_status")
                db.execSQL("DROP TABLE habit_status")
                db.execSQL("ALTER TABLE habit_status_new RENAME TO habit_status")
                db.execSQL("CREATE INDEX index_habit_status_habitId ON habit_status(habitId)")
            }
        }

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
