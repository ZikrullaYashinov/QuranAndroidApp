package zikrulla.production.quranapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import zikrulla.production.quranapp.usecase.HomeUseCase
import zikrulla.production.quranapp.usecase.SurahDetailsUseCase
import zikrulla.production.quranapp.usecase.imp.HomeUseCaseImp
import zikrulla.production.quranapp.usecase.imp.SurahDetailsUseCaseImp

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {

    @Binds
    fun bindHomeUseCase(impl: HomeUseCaseImp): HomeUseCase

    @Binds
    fun bindSurahDetailsUseCase(impl: SurahDetailsUseCaseImp): SurahDetailsUseCase
}