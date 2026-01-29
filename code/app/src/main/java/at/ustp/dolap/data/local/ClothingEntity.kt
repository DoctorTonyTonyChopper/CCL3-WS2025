package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Room entity representing one clothing item stored in the "clothes" table.
@Entity(tableName = "clothes")
data class ClothingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val category: String,
    val color: String,

    val size: String? = null,
    val season: String? = null,

    // Stored as String because Room can't persist Uri directly.
    val imageUri: String? = null
)