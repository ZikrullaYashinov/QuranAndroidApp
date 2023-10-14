package zikrulla.production.quranapp.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import zikrulla.production.quranapp.db.AppDatabase
import zikrulla.production.quranapp.db.dao.AyahUzArDao
import zikrulla.production.quranapp.db.dao.SurahDao
import zikrulla.production.quranapp.networking.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "my_db")
            .fallbackToDestructiveMigration().build()
    }

}