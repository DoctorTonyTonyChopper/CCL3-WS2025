package at.ustp.dolap.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedFilterDao {

    @Query("SELECT * FROM saved_filters ORDER BY createdAt DESC")
    fun getAllSavedFilters(): Flow<List<SavedFilterEntity>>

    @Transaction
    @Query("SELECT * FROM saved_filters ORDER BY createdAt DESC")
    fun getAllSavedFiltersWithTags(): Flow<List<SavedFilterWithTags>>

    @Transaction
    @Query("SELECT * FROM saved_filters WHERE id = :id")
    fun getSavedFilterWithTags(id: Int): Flow<SavedFilterWithTags?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedFilter(filter: SavedFilterEntity): Long

    @Update
    suspend fun updateSavedFilter(filter: SavedFilterEntity)

    @Delete
    suspend fun deleteSavedFilter(filter: SavedFilterEntity)

    // Junction ops
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTagToSavedFilter(crossRef: SavedFilterTagCrossRef)

    @Query("DELETE FROM saved_filter_tags WHERE savedFilterId = :filterId")
    suspend fun clearTagsForSavedFilter(filterId: Int)
}