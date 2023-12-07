package zikrulla.production.quranapp.usecase

import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.remote.response.Surah

interface SurahDetailsUseCase {
    fun getSurah(id: Int): Flow<Resource<List<Surah>>>
    fun getSurahDB(id: Int): Flow<List<AyahUzArEntity>>
    suspend fun insertSurah(list: List<AyahUzArEntity>)
    suspend fun saveVisibleItemPosition(surahId: Int, visibleItemPosition: Int?)
    suspend fun updateIsFavourite(ayahId: Int, isFavourite: Boolean)
    fun saveLastRead(surahId: Int)
}