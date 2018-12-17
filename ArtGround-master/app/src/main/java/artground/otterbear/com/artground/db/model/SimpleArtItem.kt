package artground.otterbear.com.artground.db.model

import android.arch.persistence.room.ColumnInfo
import java.io.Serializable
import java.util.*

data class SimpleArtItem(
        val _id: Long,
        val title: String,
        val startDate: Date,
        val endDate: Date,
        val place: String?,
        val cultCode: Long?,
        val orgLink: String?,
        val time: String?,
        val useFee: String?,
        val inquiry: String?,
        val etcDesc: String?,
        val mainImg: String?,
        @ColumnInfo(name = "name")
        val categoryName: String,
        @ColumnInfo(name = "themeColor")
        val categoryThemeColor: String
) : Serializable