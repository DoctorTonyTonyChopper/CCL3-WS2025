package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.OutfitDao
import at.ustp.dolap.data.local.OutfitEntity
import kotlinx.coroutines.flow.Flow

class OutfitRepository(
    private val outfitDao: OutfitDao
) {
    fun getAll(): Flow<List<OutfitEntity>> = outfitDao.getAllOutfits()

    fun getById(id: Int): Flow<OutfitEntity?> = outfitDao.getOutfitById(id)

    suspend fun add(outfit: OutfitEntity): Long = outfitDao.insertOutfit(outfit)

    suspend fun update(outfit: OutfitEntity) = outfitDao.updateOutfit(outfit)

    suspend fun delete(outfit: OutfitEntity) = outfitDao.deleteOutfit(outfit)
}