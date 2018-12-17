package artground.otterbear.com.artground.db.model

import android.arch.persistence.room.ColumnInfo
import java.util.*

data class DashboardReviewItem(
        val _id: Long,
        val aid: Long,
        val desc: String,
        val date: Date,
        val title: String,
        val mainImg: String?,
        @ColumnInfo(name = "name")
        val categoryName: String,
        @ColumnInfo(name = "themeColor")
        val categoryThemeColor: String
)