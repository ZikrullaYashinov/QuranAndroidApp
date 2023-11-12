package zikrulla.production.quranapp.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import zikrulla.production.quranapp.data.model.BaseResponse
import zikrulla.production.quranapp.data.remote.response.Surah
import zikrulla.production.quranapp.data.model.SurahName

interface ApiService {

    @GET("surah/{id}/editions/quran-uthmani,uz.sodik")
    suspend fun getSurah(@Path("id") id: Int): Response<BaseResponse<List<Surah>>>

    @GET("surah")
    suspend fun getSurahList(): Response<BaseResponse<List<SurahName>>>

}