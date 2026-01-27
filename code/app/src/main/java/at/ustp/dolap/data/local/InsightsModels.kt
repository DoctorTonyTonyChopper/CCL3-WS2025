package at.ustp.dolap.data.local

/**
 * Minimal stats row for an outfit based on wear log.
 * lastWorn is epochDay or null if never worn.
 */
data class OutfitWearStats(
    val outfitId: Int,
    val name: String,
    val wearCount: Int,
    val lastWorn: Long?,
    val imageUri: String? = null
)

/**
 * Minimal stats row for a clothing item based on wears of outfits that contain it.
 * lastWorn is epochDay or null if never worn.
 */
data class ClothingWearStats(
    val clothingId: Int,
    val name: String,
    val category: String?,
    val imageUri: String?,
    val wearCount: Int,
    val lastWorn: Long?,

)