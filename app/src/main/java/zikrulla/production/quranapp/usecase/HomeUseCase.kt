package zikrulla.production.quranapp.usecase

import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.model.SurahName

interface HomeUseCase {
    fun getSurahListName(): Flow<Resource<List<SurahName>>>
    fun getSurahListNameDB(): Flow<List<SurahEntity>>
    suspend fun insertSurahListName(list: List<SurahEntity>)
    fun getLastRead(): Int?
}