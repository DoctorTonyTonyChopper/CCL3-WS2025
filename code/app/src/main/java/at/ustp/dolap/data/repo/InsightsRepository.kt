package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.ClothingDao
import at.ustp.dolap.data.local.OutfitDao

// Repository that combines Outfit + Clothing DAOs to provide "insights" (stats/analytics queries).
class InsightsRepository(
    private val outfitDao: OutfitDao,
    private val clothingDao: ClothingDao
) {
    fun mostWornOutfits(limit: Int) = outfitDao.getMostWornOutfits(limit)

    fun totalWearEntries() = outfitDao.getTotalWearEntries()

    fun outfitsWornToday(todayEpochDay: Long) = outfitDao.getOutfitsWornToday(todayEpochDay)

    fun distinctOutfitsWornInRange(fromEpochDay: Long, toEpochDay: Long) =
        outfitDao.getDistinctOutfitsWornInRange(fromEpochDay, toEpochDay)

    fun leastWornClothes(limit: Int) = clothingDao.getLeastWornClothes(limit)

    fun neverWornClothes(limit: Int) = clothingDao.getNeverWornClothes(limit)

    fun clothesNotWornSince(thresholdEpochDay: Long, limit: Int) =
        clothingDao.getClothesNotWornSince(thresholdEpochDay, limit)
}