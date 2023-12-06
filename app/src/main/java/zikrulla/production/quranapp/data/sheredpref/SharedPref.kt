package zikrulla.production.quranapp.data.sheredpref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import zikrulla.production.quranapp.util.Constants.PREF
import javax.inject.Inject

class SharedPref(
    context: Context
) {

    private val sharedPref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    private val gson = GsonBuilder().create()
    fun <T> put(key: String, data: T) {
        val editor = sharedPref.edit()
        when (data) {
            is String -> editor.putString(key, data)
            is Int -> editor.putInt(key, data)
            is Long -> editor.putLong(key, data)
            is Float -> editor.putFloat(key, data)
            is Boolean -> editor.putBoolean(key, data)
            else -> editor.putString(key, gson.toJson(data))
        }
        editor.apply()
    }

    fun <T> get(key: String, clazz: Class<T>): T? {
        return when (clazz) {
            String::class.java -> sharedPref.getString(key, "")
            Int::class.java -> sharedPref.getInt(key, -1)
            Long::class.java -> sharedPref.getLong(key, -1L)
            Float::class.java -> sharedPref.getFloat(key, -1F)
            Boolean::class.java -> sharedPref.getBoolean(key, false)
            else -> {
                val json = sharedPref.getString(key, null)
                json ?: return null
                return gson.fromJson(json, clazz)
            }
        } as T
    }

}