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
import zikrulla.production.quranapp.util.NetworkHelper
import zikrulla.production.quranapp.viewmodel.SurahDetailsViewModel
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SurahDetailsViewModelImp @Inject constructor(
    private val surahDetailsUseCase: SurahDetailsUseCase,
    private val networkHelper: NetworkHelper
) : ViewModel(), SurahDetailsViewModel {

    private val _stateAyahUzAr = MutableStateFlow<SurahDetailsResource<List<AyahUzArEntity>>>(SurahDetailsResource.Loading)
    val stateAyahUzAr get() = _stateAyahUzAr.asStateFlow()

    private val _stateService = MutableStateFlow<AudioService?>(null)
    val stateService get() = _stateService.asStateFlow()

    private val _stateLastItem = MutableStateFlow<LastItem?>(null)
    val stateLastItem get() = _stateLastItem.asStateFlow()

    private val _stateLastReadIsUpdate = MutableStateFlow(false)
    val stateLastReadIsUpdate get() = _stateLastReadIsUpdate.asStateFlow()


    override fun fetchSurah(id: Int) {
        surahDetailsUseCase.getSurahDB(id).onEach {
            if (it.isNotEmpty())
                _stateAyahUzAr.value = SurahDetailsResource.Success(it)
            else {
                if (networkHelper.isNetworkConnected()) {
                    fetchSurahApi(id)
                } else {
                    _stateAyahUzAr.value = SurahDetailsResource.NotInternet
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun fetchSurahApi(id: Int) {
        surahDetailsUseCase.getSurah(id).onEach {
            when (it) {
                is Resource.Error -> {
                    _stateAyahUzAr.value = SurahDetailsResource.Error(it.e)
                }

                is Resource.Loading -> {
                    _stateAyahUzAr.value = SurahDetailsResource.Loading
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

    fun saveLastRead(ayahId: Int, isFavourite: Boolean) {
        viewModelScope.launch {
            surahDetailsUseCase.updateIsFavourite(ayahId, isFavourite)
        }
    }

    fun setLastReadIsUpdate() {
        val value = _stateLastReadIsUpdate.value
        if (!value)
            _stateLastReadIsUpdate.value = true
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
sealed class SurahDetailsResource<out T> {
    object Loading : SurahDetailsResource<Nothing>()
    object NotInternet : SurahDetailsResource<Nothing>()
    class Success<T : Any>(val data: T) : SurahDetailsResource<T>()
    class Error(val e: Throwable) : SurahDetailsResource<Nothing>()
}