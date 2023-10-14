package zikrulla.production.quranapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.db.entity.AyahUzArEntity

@Dao
interface AyahUzArDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<AyahUzArEntity>)

    @Query("SELECT * FROM ayah WHERE surahId=:surahId")
    fun getAyahsBySurahId(surahId: Int): Flow<List<AyahUzArEntity>>

}