package zikrulla.production.quranapp.viewmodel.imp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.usecase.HomeUseCase
import zikrulla.production.quranapp.viewmodel.HomeViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImp @Inject constructor(
    private val homeUseCase: HomeUseCase
) : ViewModel(), HomeViewModel {

    private val _stateSurahNameList = MutableLiveData<Resource<List<SurahEntity>>>()

    init {
        fetchSurahListNameDB()
    }

    override fun getSurahNameList() = _stateSurahNameList
    override fun fetchSurahListNameDB() {
        viewModelScope.launch {
            homeUseCase.getSurahListNameDB().onStart {
                _stateSurahNameList.postValue(Resource.Loading())
            }.catch {
                _stateSurahNameList.postValue(Resource.Error(it))
            }.collect {
                if (it.isNotEmpty())
                    _stateSurahNameList.postValue(Resource.Success(it))
                else
                    fetchSurahListName()
            }
        }
    }

    override fun fetchSurahListName() {
        viewModelScope.launch {
            homeUseCase.getSurahListName().onStart {
                _stateSurahNameList.postValue(Resource.Loading())
            }.catch {
                _stateSurahNameList.postValue(Resource.Error(it))
            }.collect { it ->
                when (it) {
                    is Resource.Error -> {
                        _stateSurahNameList.postValue(Resource.Error(it.e))
                    }

                    is Resource.Loading -> {
                        _stateSurahNameList.postValue(Resource.Loading())
                    }

                    is Resource.Success -> {
                        val list = it.data.map { it.toSurahEntity() }
                        homeUseCase.insertSurahListName(list)
                    }
                }
            }
        }
    }

}