package zikrulla.production.quranapp.model

import zikrulla.production.quranapp.db.entity.AyahUzArEntity

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
            uz.sajda,
            uz.text,
            ar.text
        )
    }
}
