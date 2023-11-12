package zikrulla.production.quranapp.usecase.imp

import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.data.model.SurahName
import zikrulla.production.quranapp.repository.QuranRepository
import zikrulla.production.quranapp.usecase.HomeUseCase
import javax.inject.Inject

class HomeUseCaseImp @Inject constructor(
    private val repository: QuranRepository
) : HomeUseCase {

    override fun getSurahListName(): Flow<Resource<List<SurahName>>> {
        return repository.getSurahListName()
    }

    override fun getSurahListNameDB(): Flow<List<SurahEntity>> {
        return repository.getSurahListNameDB()
    }

    override suspend fun insertSurahListName(list: List<SurahEntity>) {
        repository.insertSurahListName(list)
    }

}