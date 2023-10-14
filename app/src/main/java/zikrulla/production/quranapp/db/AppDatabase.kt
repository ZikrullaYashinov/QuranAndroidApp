package zikrulla.production.quranapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import zikrulla.production.quranapp.db.dao.AyahUzArDao
import zikrulla.production.quranapp.db.dao.SurahDao
import zikrulla.production.quranapp.db.entity.AyahUzArEntity
import zikrulla.production.quranapp.db.entity.SurahEntity

@Database(entities = [AyahUzArEntity::class, SurahEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ayahUzArDao(): AyahUzArDao
    abstract fun surahDao(): SurahDao

}