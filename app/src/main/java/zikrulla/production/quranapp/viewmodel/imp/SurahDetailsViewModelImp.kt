package zikrulla.production.quranapp.viewmodel.imp

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val _stateAyahUzAr = MutableStateFlow<Resource<List<AyahUzArEntity>>>(Resource.Loading)
    val stateAyahUzAr get() = _stateAyahUzAr.asStateFlow()

    private val _stateService = MutableStateFlow<AudioService?>(null)
    val stateService get() = _stateService.asStateFlow()

    private val _stateLastItem = MutableStateFlow<LastItem?>(null)
    val stateLastItem get() = _stateLastItem.asStateFlow()


    //    private val _stateVisibleItemPosition = MutableLiveData<Int?>()
    override fun fetchSurah(id: Int) {
        surahDetailsUseCase.getSurahDB(id).onEach {
            if (it.isNotEmpty())
                _stateAyahUzAr.value = Resource.Success(it)
            else
                fetchSurahApi(id)
        }.launchIn(viewModelScope)
    }

    override fun fetchSurahApi(id: Int) {
        surahDetailsUseCase.getSurah(id).onEach {
            when (it) {
                is Resource.Error -> {
                    _stateAyahUzAr.value = Resource.Error(it.e)
                }

                is Resource.Loading -> {
                    _stateAyahUzAr.value = Resource.Loading
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
        }.launchIn(viewModelScope)
    }

    override fun saveVisibleItemPosition(surahId: Int, visibleItemPosition: Int?) {
        viewModelScope.launch {
            surahDetailsUseCase.saveVisibleItemPosition(surahId, visibleItemPosition)
        }
    }

    fun saveLastRead(surahId: Int) {
        surahDetailsUseCase.saveLastRead(surahId)
    }

    fun fetchService(mBound: Boolean, audioService: AudioService? = null) {
        if (mBound && audioService != null)
            _stateService.value = audioService
        else
            _stateService.value = null
    }

    fun setLastItem(lastItem: LastItem) {
        _stateLastItem.value = lastItem
    }
}