package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class ClothingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val category: String,
    val color: String,

    val size: String? = null,
    val season: String? = null,

    // Image Uri is stored as a string
    val imageUri: String? = null
)