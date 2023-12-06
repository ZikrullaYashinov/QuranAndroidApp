package zikrulla.production.quranapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import zikrulla.production.quranapp.data.local.database.AppDatabase
import zikrulla.production.quranapp.data.sheredpref.SharedPref
import zikrulla.production.quranapp.util.Constants.PREF
import zikrulla.production.quranapp.util.Constants.PREF_LAST_READ_SURAH_ID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPrefModule {

    @Provides
    @Singleton
    fun provideGson(): Gson? {
        return GsonBuilder().create()
    }


    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPref {
        return SharedPref(context)
    }



}