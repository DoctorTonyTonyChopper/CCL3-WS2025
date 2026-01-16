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

    // --- Insights: Clothing stats (based on wears of outfits containing the clothing) ---

    @Query(
        """
        SELECT
            c.id AS clothingId,
            c.name AS name,
            c.category AS category,
            c.imageUri AS imageUri,
            COALESCE(COUNT(ow.id), 0) AS wearCount,
            MAX(ow.wornDate) AS lastWorn
        FROM clothes c
        LEFT JOIN outfit_clothes oc ON oc.clothingId = c.id
        LEFT JOIN outfit_wear ow ON ow.outfitId = oc.outfitId
        GROUP BY c.id
        ORDER BY wearCount ASC, lastWorn ASC
        LIMIT :limit
        """
    )
    fun getLeastWornClothes(limit: Int): Flow<List<ClothingWearStats>>

    @Query(
        """
        SELECT
            c.id AS clothingId,
            c.name AS name,
            c.category AS category,
            c.imageUri AS imageUri,
            0 AS wearCount,
            NULL AS lastWorn
        FROM clothes c
        WHERE NOT EXISTS (
            SELECT 1
            FROM outfit_clothes oc
            JOIN outfit_wear ow ON ow.outfitId = oc.outfitId
            WHERE oc.clothingId = c.id
        )
        ORDER BY c.id DESC
        LIMIT :limit
        """
    )
    fun getNeverWornClothes(limit: Int): Flow<List<ClothingWearStats>>

    @Query(
        """
        SELECT
            c.id AS clothingId,
            c.name AS name,
            c.category AS category,
            c.imageUri AS imageUri,
            COALESCE(COUNT(ow.id), 0) AS wearCount,
            MAX(ow.wornDate) AS lastWorn
        FROM clothes c
        LEFT JOIN outfit_clothes oc ON oc.clothingId = c.id
        LEFT JOIN outfit_wear ow ON ow.outfitId = oc.outfitId
        GROUP BY c.id
        HAVING lastWorn IS NULL OR lastWorn < :thresholdEpochDay
        ORDER BY (lastWorn IS NULL) DESC, lastWorn ASC
        LIMIT :limit
        """
    )
    fun getClothesNotWornSince(thresholdEpochDay: Long, limit: Int): Flow<List<ClothingWearStats>>



}