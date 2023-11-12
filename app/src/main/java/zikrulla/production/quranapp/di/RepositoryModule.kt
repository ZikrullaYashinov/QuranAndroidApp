package zikrulla.production.quranapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import zikrulla.production.quranapp.repository.QuranRepository
import zikrulla.production.quranapp.repository.imp.QuranRepositoryImp

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun getQuranRepository(impl: QuranRepositoryImp): QuranRepository


}