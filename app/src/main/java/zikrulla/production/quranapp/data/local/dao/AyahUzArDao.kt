package zikrulla.production.quranapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity

@Dao
interface AyahUzArDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AyahUzArEntity>)

    @Query("SELECT * FROM ayah WHERE surahId=:surahId")
    fun getAyahsBySurahId(surahId: Int): Flow<List<AyahUzArEntity>>

    @Query("UPDATE ayah SET favourite = :value WHERE number = :ayahId")
    suspend fun updateIsFavourite(ayahId: Int, value: Boolean)
}