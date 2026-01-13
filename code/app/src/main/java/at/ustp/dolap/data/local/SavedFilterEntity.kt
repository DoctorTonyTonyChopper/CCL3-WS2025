package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_filters")
data class SavedFilterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,

    // Clothing filters (null == not set)
    val category: String? = null,
    val color: String? = null,
    val size: String? = null,
    val season: String? = null,

    // Sorting presets for clothing grid
    val sortBy: String? = null,
    val sortAscending: Boolean? = null,

    /** epoch millis */
    val createdAt: Long = System.currentTimeMillis()
)