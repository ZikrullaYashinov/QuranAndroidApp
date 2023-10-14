package zikrulla.production.quranapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zikrulla.production.quranapp.db.entity.AyahUzArEntity
import zikrulla.production.quranapp.db.entity.SurahEntity

@Dao
interface SurahDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<SurahEntity>)

    @Query("SELECT * FROM surah")
    fun getSurah(): Flow<List<SurahEntity>>

}