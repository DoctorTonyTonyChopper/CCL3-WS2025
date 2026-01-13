package at.ustp.dolap.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: DolapDatabase? = null

    fun getDatabase(context: Context): DolapDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DolapDatabase::class.java,
                "dolap_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}