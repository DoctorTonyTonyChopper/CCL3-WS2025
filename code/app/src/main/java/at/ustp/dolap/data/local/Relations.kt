package at.ustp.dolap.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Room relation models: load parent entity together with its linked items via junction tables.
data class OutfitWithClothes(
    @Embedded val outfit: OutfitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = OutfitClothingCrossRef::class,
            parentColumn = "outfitId",
            entityColumn = "clothingId"
        )
    )
    val clothes: List<ClothingEntity>
)

data class ClothingWithTags(
    @Embedded val clothing: ClothingEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ClothingTagCrossRef::class,
            parentColumn = "clothingId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)

data class SavedFilterWithTags(
    @Embedded val filter: SavedFilterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SavedFilterTagCrossRef::class,
            parentColumn = "savedFilterId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)