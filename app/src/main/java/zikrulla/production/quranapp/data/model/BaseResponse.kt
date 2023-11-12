package zikrulla.production.quranapp.data.model

data class BaseResponse<T>(
    val code: Int,
    val `data`: T,
    val status: String
)