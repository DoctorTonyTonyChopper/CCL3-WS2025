package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Tag table (unique name) used to label/filter clothing and saved filter presets.
@Entity(
    tableName = "tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)