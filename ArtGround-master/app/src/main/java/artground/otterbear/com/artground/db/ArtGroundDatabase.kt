package artground.otterbear.com.artground.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import artground.otterbear.com.artground.db.dao.ArtItemDao
import artground.otterbear.com.artground.db.dao.CategoryItemDao
import artground.otterbear.com.artground.db.model.ArtItem
import artground.otterbear.com.artground.db.model.CategoryItem
import artground.otterbear.com.artground.db.model.ReviewItem
import artground.otterbear.com.artground.db.model.UserArtItem
import com.huma.room_for_asset.RoomAsset

@Database(entities = [(ArtItem::class), (CategoryItem::class), (UserArtItem::class), (ReviewItem::class)], version = 2)
@TypeConverters(value = [Converter::class])
abstract class ArtGroundDatabase : RoomDatabase() {
    companion object {
        private const val DB_NAME = "artground.db"
        private var INSTANCE: ArtGroundDatabase? = null

        fun getDatabase(context: Context): ArtGroundDatabase {
            if (INSTANCE == null) {
                synchronized(ArtGroundDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = RoomAsset.databaseBuilder(context, ArtGroundDatabase::class.java, DB_NAME).build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    abstract fun artItemDao(): ArtItemDao
    abstract fun categoryItemDao(): CategoryItemDao
}