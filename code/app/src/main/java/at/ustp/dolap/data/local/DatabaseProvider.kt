package at.ustp.dolap.data.local

import android.content.Context
import androidx.room.Room

// Provides a single (thread-safe) Room database instance for the whole app.
object DatabaseProvider {

    @Volatile
    private var INSTANCE: DolapDatabase? = null

    fun getDatabase(context: Context): DolapDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DolapDatabase::class.java,
                "dolap_db"
            )
                .addMigrations(DolapDatabase.MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
        }
    }
}