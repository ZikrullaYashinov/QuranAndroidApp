package zikrulla.production.quranapp.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import zikrulla.production.quranapp.model.BaseResponse
import zikrulla.production.quranapp.model.Surah
import zikrulla.production.quranapp.model.SurahName

interface ApiService {

    @GET("surah/{id}/editions/quran-uthmani,uz.sodik")
    suspend fun getSurah(@Path("id") id: Int): Response<BaseResponse<List<Surah>>>

    @GET("surah")
    suspend fun getSurahList(): Response<BaseResponse<List<SurahName>>>

}