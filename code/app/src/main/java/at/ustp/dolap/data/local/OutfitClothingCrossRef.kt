package at.ustp.dolap.data.local

import androidx.room.Entity
import androidx.room.Index

/** Junction table: many outfits <-> many clothing items */
@Entity(
    tableName = "outfit_clothes",
    primaryKeys = ["outfitId", "clothingId"],
    indices = [
        Index(value = ["outfitId"]),
        Index(value = ["clothingId"])
    ]
)
data class OutfitClothingCrossRef(
    val outfitId: Int,
    val clothingId: Int
)