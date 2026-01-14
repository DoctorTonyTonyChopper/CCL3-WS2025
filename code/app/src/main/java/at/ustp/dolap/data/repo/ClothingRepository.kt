package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.ClothingDao
import at.ustp.dolap.data.local.ClothingEntity

class ClothingRepository(
    private val dao: ClothingDao
) {
    fun getAll() = dao.getAllClothes()

    fun getById(id: Int) = dao.getClothingById(id)

    suspend fun add(item: ClothingEntity): Int {
        return dao.insertClothing(item).toInt()
    }

    suspend fun update(item: ClothingEntity) {
        dao.updateClothing(item)
    }

    suspend fun delete(item: ClothingEntity) {
        dao.deleteClothing(item)
    }

    fun getByAllTags(tagIds: List<Int>): kotlinx.coroutines.flow.Flow<List<ClothingEntity>> {
        return dao.getClothesWithAllTags(tagIds, tagIds.size)
    }
}