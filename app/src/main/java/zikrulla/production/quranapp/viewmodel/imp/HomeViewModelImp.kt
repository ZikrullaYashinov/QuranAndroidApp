package zikrulla.production.quranapp.viewmodel.imp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.usecase.HomeUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImp @Inject constructor(
    private val homeUseCase: HomeUseCase,
) : ViewModel() {

    private val _stateSurahNameList =
        MutableStateFlow<Resource<List<SurahEntity>>>(Resource.Loading)

    fun getSurahNameList() = _stateSurahNameList.asStateFlow()


    init {
        fetchSurahListNameDB()
    }

    private fun fetchSurahListNameDB() {
        _stateSurahNameList.value = Resource.Loading
        homeUseCase.getSurahListNameDB()
            .onEach {
                if (it.isNotEmpty())
                    _stateSurahNameList.emit(Resource.Success(it))
                else
                    fetchSurahListName()
            }.launchIn(viewModelScope)
    }

    private fun fetchSurahListName() {
        homeUseCase.getSurahListName()
            .onEach { it ->
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

}