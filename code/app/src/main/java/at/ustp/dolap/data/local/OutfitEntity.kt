package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val occasion: String? = null,
    val season: String? = null,
    val notes: String? = null,
    /** 1..5 */
    val rating: Int = 3,

    /** epoch millis */
    val createdAt: Long = System.currentTimeMillis()
)