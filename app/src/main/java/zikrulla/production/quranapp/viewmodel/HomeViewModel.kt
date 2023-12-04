package zikrulla.production.quranapp.viewmodel

import androidx.lifecycle.LiveData
import zikrulla.production.quranapp.data.local.entity.SurahEntity
import zikrulla.production.quranapp.data.model.Resource

interface HomeViewModel {
    suspend fun fetchSurahListName()
    suspend fun fetchSurahListNameDB()
}