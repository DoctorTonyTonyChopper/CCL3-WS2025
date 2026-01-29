package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.OutfitClothingCrossRef
import at.ustp.dolap.data.local.OutfitDao
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.data.local.OutfitWearEntity
import at.ustp.dolap.data.local.OutfitWithClothes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Repository layer for outfits: CRUD + managing outfit<->clothing relations + wear-log tracking.
class OutfitRepository(
    private val outfitDao: OutfitDao
) {
    fun getAll(): Flow<List<OutfitEntity>> = outfitDao.getAllOutfits()
    fun getById(id: Int): Flow<OutfitEntity?> = outfitDao.getOutfitById(id)

    suspend fun add(outfit: OutfitEntity): Long = outfitDao.insertOutfit(outfit)
    suspend fun update(outfit: OutfitEntity) = outfitDao.updateOutfit(outfit)
    suspend fun delete(outfit: OutfitEntity) = outfitDao.deleteOutfit(outfit)

    fun getAllOutfitsWithClothes() = outfitDao.getAllOutfitsWithClothes()
    fun getOutfitWithClothes(id: Int): Flow<OutfitWithClothes?> = outfitDao.getOutfitWithClothes(id)

    suspend fun addClothing(outfitId: Int, clothingId: Int) {
        outfitDao.addClothingToOutfit(OutfitClothingCrossRef(outfitId, clothingId))
    }

    suspend fun removeClothing(outfitId: Int, clothingId: Int) {
        outfitDao.removeClothingFromOutfit(outfitId, clothingId)
    }

    suspend fun setClothes(outfitId: Int, clothingIds: List<Int>) {
        outfitDao.clearOutfitClothes(outfitId)
        clothingIds.distinct().forEach { cid ->
            outfitDao.addClothingToOutfit(OutfitClothingCrossRef(outfitId, cid))
        }
    }

    // Creates an outfit and immediately attaches its clothes
    suspend fun addWithClothes(outfit: OutfitEntity, clothingIds: List<Int>): Int {
        val newId = outfitDao.insertOutfit(outfit).toInt()
        outfitDao.clearOutfitClothes(newId)
        clothingIds.distinct().forEach { cid ->
            outfitDao.addClothingToOutfit(OutfitClothingCrossRef(newId, cid))
        }
        return newId
    }

    // Wear log: stores when an outfit was worn + provides counts.
    fun getWearLogForOutfit(outfitId: Int): Flow<List<OutfitWearEntity>> =
        outfitDao.getWearLogForOutfit(outfitId)

    fun getWearCountForOutfit(outfitId: Int): Flow<Int> =
        outfitDao.getWearLogForOutfit(outfitId).map { it.size }


    suspend fun addWear(outfitId: Int, wornDateEpochDay: Long): Long =
        outfitDao.insertWear(
            OutfitWearEntity(
                outfitId = outfitId,
                wornDate = wornDateEpochDay
            )
        )

    suspend fun deleteWearById(id: Int) = outfitDao.deleteWearById(id)

    suspend fun deleteWear(entry: OutfitWearEntity) = outfitDao.deleteWear(entry)
}