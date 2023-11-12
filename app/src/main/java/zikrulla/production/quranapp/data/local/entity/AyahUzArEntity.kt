package zikrulla.production.quranapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import zikrulla.production.quranapp.data.remote.response.Ayah
import zikrulla.production.quranapp.data.model.AyahUzAr

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
    @SerializedName("textUz")
    val textUz: String,
    @SerializedName("textAr")
    val textAr: String
) {
    fun toAyahUzAr(): AyahUzAr {
        return AyahUzAr(
            Ayah(hizbQuarter, juz, manzil, number, numberInSurah, page, ruku, textUz),
            Ayah(hizbQuarter, juz, manzil, number, numberInSurah, page, ruku, textAr)
        )
    }

}
