package at.ustp.dolap.viewmodel

import at.ustp.dolap.data.local.ClothingWearStats
import at.ustp.dolap.data.local.OutfitWearStats

data class InsightsUiState(
    val isEmptyWearLog: Boolean = true,

    val totalWearEntries: Int = 0,
    val outfitsWornToday: Int = 0,
    val distinctOutfitsLast30Days: Int = 0,

    val mostWornOutfits: List<OutfitWearStats> = emptyList(),
    val neverWornClothes: List<ClothingWearStats> = emptyList(),
    val leastWornClothes: List<ClothingWearStats> = emptyList(),
    val notWornIn90Days: List<ClothingWearStats> = emptyList()
)