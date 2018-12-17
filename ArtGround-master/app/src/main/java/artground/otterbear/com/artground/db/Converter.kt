package artground.otterbear.com.artground.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converter {
    companion object {
        @JvmStatic
        @TypeConverter
        fun fromTimestamp(value: Long): Date {
            return Date(value)
        }

        @JvmStatic
        @TypeConverter
        fun dateToTimestamp(date: Date): Long {
            return date.time
        }

        @JvmStatic
        @TypeConverter
        fun fromFavoriteIntValue(value: Int): Boolean {
            return value == 1
        }

        @JvmStatic
        @TypeConverter
        fun favoriteToIntValue(favorite: Boolean): Int {
            return if (favorite) 1 else 0
        }
    }
}