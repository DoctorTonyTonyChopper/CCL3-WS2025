package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.ClothingDao
import at.ustp.dolap.data.local.ClothingEntity

class ClothingRepository(
    private val dao: ClothingDao
) {
    fun getAll() = dao.getAllClothes()

    fun getById(id: Int) = dao.getClothingById(id)

    suspend fun add(item: ClothingEntity) {
        dao.insertClothing(item)
    }

    suspend fun update(item: ClothingEntity) {
        dao.updateClothing(item)
    }

    suspend fun delete(item: ClothingEntity) {
        dao.deleteClothing(item)
    }
}