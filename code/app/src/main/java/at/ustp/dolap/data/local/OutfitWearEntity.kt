package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Stores each time an outfit was worn (used for statistics and insights).
@Entity(
    tableName = "outfit_wear",
    indices = [
        Index(value = ["outfitId"]),
        Index(value = ["wornDate"])
    ]
)
data class OutfitWearEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val outfitId: Int,
    /** UTC epoch day (LocalDate.toEpochDay()) */
    val wornDate: Long
)