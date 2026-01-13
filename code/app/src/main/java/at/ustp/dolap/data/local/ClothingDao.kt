package at.ustp.dolap.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {

    // Read all
    @Query("SELECT * FROM clothes ORDER BY id DESC")
    fun getAllClothes(): Flow<List<ClothingEntity>>

    // Read one (for Detail screen)
    @Query("SELECT * FROM clothes WHERE id = :id")
    fun getClothingById(id: Int): Flow<ClothingEntity?>

    // Create
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(item: ClothingEntity)


    @Update
    suspend fun updateClothing(item: ClothingEntity)


    @Delete
    suspend fun deleteClothing(item: ClothingEntity)
}