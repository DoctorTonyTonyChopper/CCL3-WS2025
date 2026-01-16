package at.ustp.dolap.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.*

@Dao
interface OutfitDao {

    // --- Outfit CRUD ---

    @Query("SELECT * FROM outfits ORDER BY createdAt DESC")
    fun getAllOutfits(): Flow<List<OutfitEntity>>

    @Query("SELECT * FROM outfits WHERE id = :id")
    fun getOutfitById(id: Int): Flow<OutfitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitEntity): Long

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Delete
    suspend fun deleteOutfit(outfit: OutfitEntity)

    // --- Many-to-many: outfit <-> clothes ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addClothingToOutfit(crossRef: OutfitClothingCrossRef)

    @Query("DELETE FROM outfit_clothes WHERE outfitId = :outfitId AND clothingId = :clothingId")
    suspend fun removeClothingFromOutfit(outfitId: Int, clothingId: Int)

    @Query("DELETE FROM outfit_clothes WHERE outfitId = :outfitId")
    suspend fun clearOutfitClothes(outfitId: Int)

    @Transaction
    @Query("SELECT * FROM outfits ORDER BY createdAt DESC")
    fun getAllOutfitsWithClothes(): Flow<List<OutfitWithClothes>>

    @Transaction
    @Query("SELECT * FROM outfits WHERE id = :id")
    fun getOutfitWithClothes(id: Int): Flow<OutfitWithClothes?>

    // --- Wear log CRUD ---
    @Query("SELECT * FROM outfit_wear WHERE outfitId = :outfitId ORDER BY wornDate DESC")
    fun getWearLogForOutfit(outfitId: Int): Flow<List<OutfitWearEntity>>

    @Query("SELECT * FROM outfit_wear ORDER BY wornDate DESC")
    fun getAllWearLog(): Flow<List<OutfitWearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWear(entry: OutfitWearEntity): Long

    @Delete
    suspend fun deleteWear(entry: OutfitWearEntity)

    @Query("DELETE FROM outfit_wear WHERE id = :id")
    suspend fun deleteWearById(id: Int)

    // --- Insights: Outfit stats ---

    @Query(
        """
        SELECT 
            o.id AS outfitId,
            o.name AS name,
            COUNT(ow.id) AS wearCount,
            MAX(ow.wornDate) AS lastWorn
        FROM outfits o
        LEFT JOIN outfit_wear ow ON ow.outfitId = o.id
        GROUP BY o.id
        ORDER BY wearCount DESC, lastWorn DESC
        LIMIT :limit
        """
    )
    fun getMostWornOutfits(limit: Int): Flow<List<OutfitWearStats>>

    @Query("SELECT COUNT(*) FROM outfit_wear")
    fun getTotalWearEntries(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT outfitId) FROM outfit_wear WHERE wornDate = :todayEpochDay")
    fun getOutfitsWornToday(todayEpochDay: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(DISTINCT outfitId) FROM outfit_wear
        WHERE wornDate >= :fromEpochDay AND wornDate <= :toEpochDay
        """
    )
    fun getDistinctOutfitsWornInRange(fromEpochDay: Long, toEpochDay: Long): Flow<Int>
}