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
interface TagDao {


    @Query("SELECT * FROM tags ORDER BY name COLLATE NOCASE")
    fun getAllTags(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: TagEntity): Long

    @Query("SELECT * FROM tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): TagEntity?

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Delete
    suspend fun deleteTag(tag: TagEntity)

    @Query("SELECT * FROM tags WHERE id = :id")
    fun getTagById(id: Int): Flow<TagEntity?>

    // --- Many-to-many: clothing <-> tags ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTagToClothing(crossRef: ClothingTagCrossRef)

    @Query("DELETE FROM clothing_tags WHERE clothingId = :clothingId AND tagId = :tagId")
    suspend fun removeTagFromClothing(clothingId: Int, tagId: Int)

    @Query("DELETE FROM clothing_tags WHERE clothingId = :clothingId")
    suspend fun clearTagsForClothing(clothingId: Int)

    @Transaction
    @Query("SELECT * FROM clothes WHERE id = :clothingId")
    fun getClothingWithTags(clothingId: Int): Flow<ClothingWithTags?>
}