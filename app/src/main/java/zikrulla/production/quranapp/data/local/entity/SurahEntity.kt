package zikrulla.production.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
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
    val revelationType: String,
    @SerializedName("lastReadAyah")
    val lastReadAyah: Int? = null
) : Serializable{
    override fun toString(): String {
        return """SurahEntity(englishName = "${this.englishName}", englishNameTranslation = "${this.englishNameTranslation}", number = ${this.number}, numberOfAyahs = ${this.numberOfAyahs}, revelationType = "${this.revelationType}", name = "${this.name}")""".trimMargin()
    }
}
