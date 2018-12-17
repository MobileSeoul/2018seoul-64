package artground.otterbear.com.artground.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "ArtItem")
data class ArtItem(
        @PrimaryKey(autoGenerate = true)
        var _id: Long? = null,
        val title: String,
        val startDate: Date,
        val endDate: Date,
        val time: String? = null,
        val support: String? = null,
        val useFee: String? = null,
        val orgLink: String? = null,
        val mainImg: String? = null,
        val gcode: String? = null,
        val cid: Int? = null,
        val sponsor: String? = null,
        val homepage: String? = null,
        val etcDesc: String? = null,
        val inquiry: String? = null,
        val agelimit: String? = null,
        val useTarget: String? = null,
        val cultCode: Long? = null,
        val place: String? = null,
        val isFree: String? = null
)