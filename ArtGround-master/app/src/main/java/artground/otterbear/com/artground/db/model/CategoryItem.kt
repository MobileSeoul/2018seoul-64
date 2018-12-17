package artground.otterbear.com.artground.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "CategoryItem")
data class CategoryItem(
        @PrimaryKey(autoGenerate = false)
        var _id: Long? = null,
        val name: String,
        val imgResName: String,
        var favorite: Boolean,
        var themeColor: String
)