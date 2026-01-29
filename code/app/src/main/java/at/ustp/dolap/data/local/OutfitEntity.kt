package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room entity representing a saved outfit with optional metadata and rating.
@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val occasion: String? = null,
    val season: String? = null,
    val notes: String? = null,

    /** User rating (1–5) */
    val rating: Int = 3,

    /** Creation timestamp (epoch millis) */
    val createdAt: Long = System.currentTimeMillis()
)