package zikrulla.production.quranapp.repository.imp

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zikrulla.production.quranapp.data.local.database.AppDatabase
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.model.SurahName
import zikrulla.production.quranapp.data.remote.api.ApiService
import zikrulla.production.quranapp.data.remote.response.Surah
import zikrulla.production.quranapp.data.sheredpref.SharedPref
import zikrulla.production.quranapp.repository.QuranRepository
import zikrulla.production.quranapp.util.Constants
import javax.inject.Inject

class QuranRepositoryImp @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase,
    private val sharedPref: SharedPref
) : QuranRepository {

    override fun getSurah(id: Int): Flow<Resource<List<Surah>>> {
        return flow {
            val response = apiService.getSurah(id)
            if (response.isSuccessful)
                emit(Resource.Success(response.body()?.data!!))
            else
                emit(Resource.Error(Throwable(response.message())))
        }
    }

    override fun getSurahListName(): Flow<Resource<List<SurahName>>> {
        return flow {
            val response = apiService.getSurahList()
            if (response.isSuccessful)
                emit(Resource.Success(response.body()?.data!!))
            else
                emit(Resource.Error(Throwable(response.message())))
        }
    }

    override fun getSurahDB(id: Int): Flow<List<AyahUzArEntity>> {
        return appDatabase.ayahUzArDao().getAyahsBySurahId(id)
    }

    override fun getSurahListNameDB(): Flow<List<SurahEntity>> {
        return appDatabase.surahDao().getSurah()
    }

    override suspend fun insertSurah(list: List<AyahUzArEntity>) {
        appDatabase.ayahUzArDao().insertAll(list)
    }

    override suspend fun insertSurahListName(list: List<SurahEntity>) {
        appDatabase.surahDao().insertAll(list)
    }

    override suspend fun updateSurah(surahId: Int, visibleItemPosition: Int?) {
        appDatabase.surahDao().updateSurah(surahId, visibleItemPosition)
    }

    override fun getLastReadShP(): Int? {
        return sharedPref.get(Constants.PREF_LAST_READ_SURAH_ID, Int::class.java)
    }

    override fun saveLastReadShP(id: Int) {
        sharedPref.put(Constants.PREF_LAST_READ_SURAH_ID, id)
    }

}