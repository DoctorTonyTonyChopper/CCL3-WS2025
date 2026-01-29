package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index

// Junction table for the many-to-many relationship between clothes and tags.
@Entity(
    tableName = "clothing_tags",
    primaryKeys = ["clothingId", "tagId"],
    indices = [
        Index(value = ["clothingId"]),
        Index(value = ["tagId"])
    ]
)
data class ClothingTagCrossRef(
    val clothingId: Int,
    val tagId: Int
)