package zikrulla.production.quranapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import zikrulla.production.quranapp.data.local.dao.AyahUzArDao
import zikrulla.production.quranapp.data.local.dao.SurahDao
import zikrulla.production.quranapp.data.local.entity.AyahUzArEntity
import zikrulla.production.quranapp.data.local.entity.SurahEntity

@Database(entities = [AyahUzArEntity::class, SurahEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ayahUzArDao(): AyahUzArDao
    abstract fun surahDao(): SurahDao

}