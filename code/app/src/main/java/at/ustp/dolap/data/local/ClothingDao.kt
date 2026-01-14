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
    suspend fun insertClothing(item: ClothingEntity): Long

    @Update
    suspend fun updateClothing(item: ClothingEntity)


    @Delete
    suspend fun deleteClothing(item: ClothingEntity)

    @Query(
        """
    SELECT c.* FROM clothes c
    JOIN clothing_tags ct ON ct.clothingId = c.id
    WHERE ct.tagId IN (:tagIds)
    GROUP BY c.id
    HAVING COUNT(DISTINCT ct.tagId) = :tagCount
    """
    )
    fun getClothesWithAllTags(tagIds: List<Int>, tagCount: Int): Flow<List<ClothingEntity>>



}