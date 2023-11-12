package zikrulla.production.quranapp.viewmodel

import androidx.lifecycle.LiveData
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource

interface HomeViewModel {
    fun getSurahNameList(): LiveData<Resource<List<SurahEntity>>>
    fun fetchSurahListName()
    fun fetchSurahListNameDB()
}