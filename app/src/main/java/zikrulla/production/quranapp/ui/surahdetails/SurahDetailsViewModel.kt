package zikrulla.production.quranapp.ui.surahdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.db.entity.AyahUzArEntity
import zikrulla.production.quranapp.model.AyahUzAr
import zikrulla.production.quranapp.model.Resource
import zikrulla.production.quranapp.model.SurahName
import zikrulla.production.quranapp.repository.QuranRepository
import javax.inject.Inject

@HiltViewModel
class SurahDetailsViewModel @Inject constructor(
    private val repository: QuranRepository
) : ViewModel() {

    private val _stateSurah = MutableLiveData<Resource<SurahName>>()
    private val _stateAyahUzAr = MutableLiveData<Resource<List<AyahUzArEntity>>>()

    fun getSurah() = _stateSurah
    fun getAyahUzAr() = _stateAyahUzAr

    fun fetchSurah(id: Int) {
        viewModelScope.launch {
            repository.getSurahDB(id).onStart {
                _stateAyahUzAr.postValue(Resource.Loading())
            }.catch {
                _stateAyahUzAr.postValue(Resource.Error(it))
            }.collect {
                if (it.isNotEmpty()) {
                    _stateAyahUzAr.postValue(Resource.Success(it))
                } else {
                    fetchSurahApi(id)
                }
            }
        }
    }

    private fun fetchSurahApi(id: Int) {
        viewModelScope.launch {
            repository.getSurah(id).onStart {
                _stateSurah.postValue(Resource.Loading())
            }.catch {
                _stateSurah.postValue(Resource.Error(it))
            }.collect {
                when (it) {
                    is Resource.Error -> {
                        _stateSurah.postValue(Resource.Error(it.e))
                    }

                    is Resource.Loading -> {
                        _stateSurah.postValue(Resource.Loading())
                    }

                    is Resource.Success -> {
                        val list = ArrayList<AyahUzArEntity>()
                        for (i in it.data[0].ayahs.indices)
                            list.add(
                                AyahUzAr(
                                    it.data[1].ayahs[i],
                                    it.data[0].ayahs[i]
                                ).toAyahUzArEntity(id)
                            )
                        repository.insertSurah(list)
                    }
                }
            }
        }
    }

}