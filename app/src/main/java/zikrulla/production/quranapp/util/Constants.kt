package zikrulla.production.quranapp.util

object Constants {
    val EDITIONS = mapOf(
        Edition.ALAFASY to "ar.alafasy",
        Edition.KHALIFAALTUNAIJI to "ar.khalifaaltunaiji"
    )

    const val TAG = "@@@@"

    const val DATA_URL = "url"
    const val DATA_URI = "uri"

    const val STATE_CREATED = "created"

    const val SURAH_NUMBER = "surah_number"

    const val BASE_URL = "https://api.alquran.cloud/v1/"
//    const val URL = "http://api.alquran.cloud/v1/surah/114/editions/quran-uthmani,en.asad,uz.sodik"
//    https://cdn.islamic.network/quran/audio-surah/128/{edition}/{number}.mp3

    const val URL_SURAH = "https://cdn.islamic.network/quran/audio-surah/128/"
    const val URL_AYAH = "https://cdn.islamic.network/quran/audio/128/"

    const val ARG_SURAH_NAME = "surah_name"

    const val ITEM_AYAH = 0
    const val ITEM_SURAH_INFO = 1

    const val ITEM_SURAH_NAME = 0
    const val ITEM_SURAH_LAST_READ = 1

    const val PREF = "my_pref"
    const val PREF_LAST_READ_SURAH_ID = "last_read_surah_id"
}

