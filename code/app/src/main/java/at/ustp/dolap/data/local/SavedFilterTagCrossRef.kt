package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index

// Junction table linking saved filter presets to their selected tags.
@Entity(
    tableName = "saved_filter_tags",
    primaryKeys = ["savedFilterId", "tagId"],
    indices = [
        Index(value = ["savedFilterId"]),
        Index(value = ["tagId"])
    ]
)
data class SavedFilterTagCrossRef(
    val savedFilterId: Int,
    val tagId: Int
)