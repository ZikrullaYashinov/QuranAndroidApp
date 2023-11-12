package zikrulla.production.quranapp.data.model

import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.remote.response.Ayah

data class AyahUzAr(
    val uz: Ayah,
    val ar: Ayah
) {

    fun toAyahUzArEntity(id: Int): AyahUzArEntity {
        return AyahUzArEntity(
            id,
            uz.hizbQuarter,
            uz.juz,
            uz.manzil,
            uz.number,
            uz.numberInSurah,
            uz.page,
            uz.ruku,
            uz.text,
            ar.text
        )
    }
}
