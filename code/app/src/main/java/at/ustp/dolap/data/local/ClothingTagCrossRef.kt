package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index

/** Junction table: many clothing items <-> many tags */
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