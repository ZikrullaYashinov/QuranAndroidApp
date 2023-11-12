package zikrulla.production.quranapp.data.model

import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity

data class AyahItem(
    val ayahUzArEntity: AyahUzArEntity,
    var playing: Boolean = false
)
