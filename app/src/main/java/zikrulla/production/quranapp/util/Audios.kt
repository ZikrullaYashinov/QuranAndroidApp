package zikrulla.production.quranapp.util

import zikrulla.production.quranapp.util.Constants.EDITIONS
import zikrulla.production.quranapp.util.Constants.URL_AYAH
import zikrulla.production.quranapp.util.Constants.URL_SURAH

object Audios {

//    https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/114.mp3
    fun getSurah(number: Int, edition: Edition) = "$URL_SURAH${EDITIONS[edition]}/$number.mp3"
    fun getAyah(number: Int, edition: Edition) = "$URL_AYAH${EDITIONS[edition]}/$number.mp3"
}