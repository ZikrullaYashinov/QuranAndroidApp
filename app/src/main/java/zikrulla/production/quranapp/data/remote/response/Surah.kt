package zikrulla.production.quranapp.data.remote.response

import zikrulla.production.quranapp.data.model.Edition

data class Surah(
    var ayahs: List<Ayah>,
    val edition: Edition,
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String
)