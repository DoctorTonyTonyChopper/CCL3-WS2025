package at.ustp.dolap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ClothingEntity::class],
    version = 1
)
abstract class DolapDatabase : RoomDatabase() {
    abstract fun clothingDao(): ClothingDao
}