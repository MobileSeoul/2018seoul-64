package artground.otterbear.com.artground.main

import android.content.Context
import com.huma.room_for_asset.defaultSharedPreferences

private const val FILENAME = "prefs"
private const val PREF_TEXT = "Settings"

class AGPreferences {
    //    private val prefs: SharedPreferences = context.getSharedPreferences(FILENAME, 0)
//    var text: String
//        get() = prefs.getString(PREF_TEXT, "")
//        set(value) = prefs.edit().putString(PREF_TEXT, value).apply()
    companion object {
        private const val KEY_PREPERATION_COMPLETED = "key_preperation_completed"

        fun isPreperationCompleted(context: Context) = context.defaultSharedPreferences.getBoolean(KEY_PREPERATION_COMPLETED, false)
        fun setPreperationComplete(context: Context) {
            context.defaultSharedPreferences.edit().putBoolean(KEY_PREPERATION_COMPLETED, true).apply()
        }
    }
}