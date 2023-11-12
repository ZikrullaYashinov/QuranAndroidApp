package zikrulla.production.quranapp.viewmodel.imp

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.model.AyahUzAr
import zikrulla.production.quranapp.data.model.LastItem
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.service.AudioService
import zikrulla.production.quranapp.usecase.SurahDetailsUseCase
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.viewmodel.SurahDetailsViewModel
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SurahDetailsViewModelImp @Inject constructor(
    private val surahDetailsUseCase: SurahDetailsUseCase
) : ViewModel(), SurahDetailsViewModel {

    private val _stateAyahUzAr = MutableLiveData<Resource<List<AyahUzArEntity>>>()
    private val _stateService = MutableLiveData<AudioService?>()
    private val _stateLastItem = MutableLiveData<LastItem?>()

    override fun getAyahUzAr() = _stateAyahUzAr
    override fun getService() = _stateService
    fun getLastAudioUrl() = _stateLastItem
    override fun fetchSurah(id: Int) {
        viewModelScope.launch {
            surahDetailsUseCase.getSurahDB(id).onStart {
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

    override fun fetchSurahApi(id: Int) {
        viewModelScope.launch {
            surahDetailsUseCase.getSurah(id).onStart {
                _stateAyahUzAr.postValue(Resource.Loading())
            }.catch {
                _stateAyahUzAr.postValue(Resource.Error(it))
            }.collect {
                when (it) {
                    is Resource.Error -> {
                        _stateAyahUzAr.postValue(Resource.Error(it.e))
                    }

                    is Resource.Loading -> {
                        _stateAyahUzAr.postValue(Resource.Loading())
                    }

                    is Resource.Success -> {
                        Log.d(TAG, "fetchSurahApi: ${it.data}")
                        val list = ArrayList<AyahUzArEntity>()
                        for (i in it.data[0].ayahs.indices)
                            list.add(
                                AyahUzAr(
                                    it.data[1].ayahs[i],
                                    it.data[0].ayahs[i]
                                ).toAyahUzArEntity(id)
                            )
                        surahDetailsUseCase.insertSurah(list)
                    }
                }
            }
        }
    }

    fun fetchService(mBound: Boolean, audioService: AudioService? = null) {
        if (mBound && audioService != null) {
            _stateService.postValue(audioService)
        } else {
            _stateService.postValue(null)
        }
    }

    fun setLastItem(lastItem: LastItem) {
        _stateLastItem.postValue(lastItem)
    }
}