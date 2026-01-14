package at.ustp.dolap.data.repo

import at.ustp.dolap.data.local.ClothingTagCrossRef
import at.ustp.dolap.data.local.TagDao
import at.ustp.dolap.data.local.TagEntity
import kotlinx.coroutines.flow.Flow

class TagRepository(
    private val dao: TagDao
) {
    fun getAllTags(): Flow<List<TagEntity>> = dao.getAllTags()

    fun getClothingWithTags(clothingId: Int) = dao.getClothingWithTags(clothingId)

    suspend fun ensureTagId(nameRaw: String): Int {
        val name = nameRaw.trim()
        if (name.isBlank()) throw IllegalArgumentException("Tag name blank")

        val insertedId = dao.insertTag(TagEntity(name = name))
        if (insertedId > 0) return insertedId.toInt()

        // Already existed (IGNORE) → fetch existing id
        val existing = dao.getTagByName(name)
        return existing?.id ?: throw IllegalStateException("Tag exists but cannot be loaded")
    }

    suspend fun setTagsForClothing(clothingId: Int, tagIds: Set<Int>) {
        dao.clearTagsForClothing(clothingId)
        tagIds.forEach { tagId ->
            dao.addTagToClothing(ClothingTagCrossRef(clothingId = clothingId, tagId = tagId))
        }
    }
}