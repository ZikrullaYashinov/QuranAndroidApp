package zikrulla.production.quranapp.usecase.imp

import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.remote.response.Surah
import zikrulla.production.quranapp.repository.QuranRepository
import zikrulla.production.quranapp.usecase.SurahDetailsUseCase
import javax.inject.Inject

class SurahDetailsUseCaseImp @Inject constructor(
    private val repository: QuranRepository
) : SurahDetailsUseCase {
    override fun getSurah(id: Int): Flow<Resource<List<Surah>>> {
        return repository.getSurah(id)
    }

    override fun getSurahDB(id: Int): Flow<List<AyahUzArEntity>> {
        return repository.getSurahDB(id)
    }

    override suspend fun insertSurah(list: List<AyahUzArEntity>) {
        repository.insertSurah(list)
    }

    override suspend fun saveVisibleItemPosition(surahId: Int, visibleItemPosition: Int?) {
        repository.updateSurah(surahId, visibleItemPosition)
    }

    override fun saveLastRead(surahId: Int) {
        repository.saveLastReadShP(surahId)
    }


}