package zikrulla.production.quranapp.repository

import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.model.SurahName
import zikrulla.production.quranapp.data.remote.response.Surah

interface QuranRepository {

    fun getSurah(id: Int): Flow<Resource<List<Surah>>>
    fun getSurahListName(): Flow<Resource<List<SurahName>>>
    fun getSurahDB(id: Int): Flow<List<AyahUzArEntity>>
    fun getSurahListNameDB(): Flow<List<SurahEntity>>
    suspend fun insertSurah(list: List<AyahUzArEntity>)
    suspend fun insertSurahListName(list: List<SurahEntity>)
    suspend fun updateSurah(surahId: Int, visibleItemPosition: Int?)
    fun getLastReadShP(): Int?
    fun saveLastReadShP(id: Int)

}