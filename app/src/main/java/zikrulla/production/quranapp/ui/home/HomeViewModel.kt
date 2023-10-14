package zikrulla.production.quranapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.db.entity.SurahEntity
import zikrulla.production.quranapp.model.Resource
import zikrulla.production.quranapp.repository.QuranRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _stateSurahNameList = MutableLiveData<Resource<List<SurahEntity>>>()

    init {
        fetchSurahNameList()
    }

    fun getSurahNameList() = _stateSurahNameList

    private fun fetchSurahNameList() {
        viewModelScope.launch {
            repository.getSurahListNameDB().onStart {
                _stateSurahNameList.postValue(Resource.Loading())
            }.catch {
                _stateSurahNameList.postValue(Resource.Error(it))
            }.collect {
                if (it.isNotEmpty())
                    _stateSurahNameList.postValue(Resource.Success(it))
                else
                    fetchSurahNameListApi()
            }
        }
    }

    private fun fetchSurahNameListApi() {
        viewModelScope.launch {
            repository.getSurahListName().onStart {
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
                        repository.insertSurahListName(list)
//                        _stateSurahNameList.postValue(Resource.Success(list))
                    }
                }
            }
        }
    }

}