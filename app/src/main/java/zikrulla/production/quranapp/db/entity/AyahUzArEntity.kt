package zikrulla.production.quranapp.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import zikrulla.production.quranapp.model.Ayah
import zikrulla.production.quranapp.model.AyahUzAr

@Entity(tableName = "ayah")
data class AyahUzArEntity(
    @SerializedName("surahId")
    val surahId: Int,
    @SerializedName("hizbQuarter")
    val hizbQuarter: Int,
    @SerializedName("juz")
    val juz: Int,
    @SerializedName("manzil")
    val manzil: Int,
    @SerializedName("number")
    @PrimaryKey
    val number: Int,
    @SerializedName("numberInSurah")
    val numberInSurah: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("ruku")
    val ruku: Int,
    @SerializedName("sajda")
    val sajda: Boolean,
    @SerializedName("textUz")
    val textUz: String,
    @SerializedName("textAr")
    val textAr: String
) {
    fun toAyahUzAr(): AyahUzAr {
        return AyahUzAr(
            Ayah(hizbQuarter, juz, manzil, number, numberInSurah, page, ruku, sajda, textUz),
            Ayah(hizbQuarter, juz, manzil, number, numberInSurah, page, ruku, sajda, textAr)
        )
    }

}
