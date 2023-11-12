package zikrulla.production.quranapp.viewmodel

import androidx.lifecycle.LiveData
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.model.Resource
import zikrulla.production.quranapp.service.AudioService

interface SurahDetailsViewModel {
    fun getAyahUzAr(): LiveData<Resource<List<AyahUzArEntity>>>
    fun getService(): LiveData<AudioService?>
    fun fetchSurah(id: Int)
    fun fetchSurahApi(id: Int)
    fun saveVisibleItemPosition(surahId: Int, visibleItemPosition: Int?)
}