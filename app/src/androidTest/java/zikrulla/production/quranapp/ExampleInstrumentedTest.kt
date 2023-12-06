package zikrulla.production.quranapp

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.GsonBuilder

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.sheredpref.SharedPref
import zikrulla.production.quranapp.util.Constants

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun tests() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("zikrulla.production.quranapp", appContext.packageName)

        val sharedPref = SharedPref(
            appContext.getSharedPreferences(Constants.PREF, Context.MODE_PRIVATE),
            GsonBuilder().create()
        )
        val surahEntity = SurahEntity(
            englishName = "Al-Faatiha",
            englishNameTranslation = "The Opening",
            number = 1,
            numberOfAyahs = 7,
            revelationType = "Meccan",
            name = "سُورَةُ ٱلْفَاتِحَةِ"
        )
        sharedPref.put(Constants.PREF_LAST_READ_SURAH_ID, surahEntity)
    }
}
