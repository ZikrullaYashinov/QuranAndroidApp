package zikrulla.production.quranapp.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zikrulla.production.quranapp.db.AppDatabase
import zikrulla.production.quranapp.db.entity.AyahUzArEntity
import zikrulla.production.quranapp.db.entity.SurahEntity
import zikrulla.production.quranapp.model.AyahUzAr
import zikrulla.production.quranapp.model.Resource
import zikrulla.production.quranapp.model.Surah
import zikrulla.production.quranapp.model.SurahName
import zikrulla.production.quranapp.networking.ApiService
import javax.inject.Inject

class QuranRepository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) {

    fun getSurah(id: Int): Flow<Resource<List<Surah>>> {
        return flow {
            val response = apiService.getSurah(id)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(Throwable(response.message())))
            }
        }
    }

    fun getSurahListName(): Flow<Resource<List<SurahName>>> {
        return flow {
            val response = apiService.getSurahList()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(Throwable(response.message())))
            }
        }
    }

    fun getSurahDB(id: Int): Flow<List<AyahUzArEntity>> {
        return appDatabase.ayahUzArDao().getAyahsBySurahId(id)
    }

    fun getSurahListNameDB(): Flow<List<SurahEntity>> {
        return appDatabase.surahDao().getSurah()
    }

    suspend fun insertSurah(list: List<AyahUzArEntity>){
        appDatabase.ayahUzArDao().insertAll(list)
    }

    suspend fun insertSurahListName(list: List<SurahEntity>){
        appDatabase.surahDao().insertAll(list)
    }

}