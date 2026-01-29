package at.ustp.dolap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ClothingEntity::class,
        OutfitEntity::class,
        OutfitClothingCrossRef::class,
        OutfitWearEntity::class,
        TagEntity::class,
        ClothingTagCrossRef::class,
        SavedFilterEntity::class,
        SavedFilterTagCrossRef::class,
    ],
    version = 2
)
abstract class DolapDatabase : RoomDatabase() {
    abstract fun clothingDao(): ClothingDao

    abstract fun outfitDao(): OutfitDao
    abstract fun tagDao(): TagDao
    abstract fun savedFilterDao(): SavedFilterDao

    companion object {
        /**
         * v1 -> v2: adds outfits, wear log, tags, saved filters, and junction tables.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS outfits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        occasion TEXT,
                        season TEXT,
                        notes TEXT,
                        rating INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS outfit_clothes (
                        outfitId INTEGER NOT NULL,
                        clothingId INTEGER NOT NULL,
                        PRIMARY KEY(outfitId, clothingId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_outfit_clothes_outfitId ON outfit_clothes(outfitId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_outfit_clothes_clothingId ON outfit_clothes(clothingId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS outfit_wear (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        outfitId INTEGER NOT NULL,
                        wornDate INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_outfit_wear_outfitId ON outfit_wear(outfitId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_outfit_wear_wornDate ON outfit_wear(wornDate)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS tags (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tags_name ON tags(name)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS clothing_tags (
                        clothingId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(clothingId, tagId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_clothing_tags_clothingId ON clothing_tags(clothingId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_clothing_tags_tagId ON clothing_tags(tagId)")

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_filters (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        category TEXT,
                        color TEXT,
                        size TEXT,
                        season TEXT,
                        sortBy TEXT,
                        sortAscending INTEGER,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS saved_filter_tags (
                        savedFilterId INTEGER NOT NULL,
                        tagId INTEGER NOT NULL,
                        PRIMARY KEY(savedFilterId, tagId)
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_filter_tags_savedFilterId ON saved_filter_tags(savedFilterId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_saved_filter_tags_tagId ON saved_filter_tags(tagId)")
            }
        }
    }
}