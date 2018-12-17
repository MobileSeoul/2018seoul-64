package artground.otterbear.com.artground.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(foreignKeys = arrayOf(ForeignKey(entity = ArtItem::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("aid"),
        onDelete = ForeignKey.CASCADE)))
data class ReviewItem(
        @PrimaryKey(autoGenerate = true)
        var _id: Long? = null,
        val aid: Long,
        val desc: String,
        val date: Date
)