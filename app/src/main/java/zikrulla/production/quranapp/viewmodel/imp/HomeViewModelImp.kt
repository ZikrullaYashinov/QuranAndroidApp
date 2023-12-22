package zikrulla.production.quranapp.viewmodel.imp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.usecase.HomeUseCase
import zikrulla.production.quranapp.util.Constants.TAG
import zikrulla.production.quranapp.util.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImp @Inject constructor(
    private val homeUseCase: HomeUseCase, private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _stateSurahNameList =
        MutableStateFlow<HomeResource<List<SurahEntity>>>(HomeResource.Loading)

    fun getSurahNameList() = _stateSurahNameList.asStateFlow()


    fun fetchSurahListNameDB() {
        viewModelScope.launch {
            _stateSurahNameList.value = HomeResource.Loading
            homeUseCase.getSurahListNameDB().catch {
                _stateSurahNameList.emit(HomeResource.Error(it))
            }.collect {
                if (it.isNotEmpty()) _stateSurahNameList.emit(HomeResource.Success(it))
                else {
                    if (networkHelper.isNetworkConnected()) fetchSurahListName()
                    else _stateSurahNameList.value = HomeResource.NotInternet
                }
            }
        }
    }

    private fun fetchSurahListName() {
        homeUseCase.getSurahListName().onEach { it ->
            when (it) {
                is Resource.Error -> {
//                    TODO()
                }

                Resource.Loading -> {
//                    TODO()
                }

                is Resource.Success -> {
                    val list = it.data.map { it.toSurahEntity() }
                    homeUseCase.insertSurahListName(list)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getLastRead(): Int? {
        return homeUseCase.getLastRead()
    }

}

sealed class HomeResource<out T> {
    object Loading : HomeResource<Nothing>()
    object NotInternet : HomeResource<Nothing>()
    class Success<T : Any>(val data: T) : HomeResource<T>()
    class Error(val e: Throwable) : HomeResource<Nothing>()
}