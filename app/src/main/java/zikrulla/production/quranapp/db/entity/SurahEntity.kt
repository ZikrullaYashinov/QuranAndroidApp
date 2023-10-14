package zikrulla.production.quranapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import zikrulla.production.quranapp.model.SurahName
import java.io.Serializable

@Entity(tableName = "surah")
data class SurahEntity(
    @SerializedName("englishName")
    val englishName: String,
    @SerializedName("englishNameTranslation")
    val englishNameTranslation: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    @PrimaryKey
    val number: Int,
    @SerializedName("numberOfAyahs")
    val numberOfAyahs: Int,
    @SerializedName("revelationType")
    val revelationType: String
) : Serializable {
    fun toSurahName(): SurahName {
        return SurahName(
            englishName,
            englishNameTranslation,
            name,
            number,
            numberOfAyahs,
            revelationType
        )
    }
}
