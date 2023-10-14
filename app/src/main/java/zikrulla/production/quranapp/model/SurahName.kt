package zikrulla.production.quranapp.model

import zikrulla.production.quranapp.db.entity.SurahEntity
import java.io.Serializable

data class SurahName(
    val englishName: String,
    val englishNameTranslation: String,
    val name: String,
    val number: Int,
    val numberOfAyahs: Int,
    val revelationType: String
) : Serializable {
    fun toSurahEntity(): SurahEntity {
        return SurahEntity(
            englishName,
            englishNameTranslation,
            name,
            number,
            numberOfAyahs,
            revelationType
        )
    }
}