package com.amorgens.di

import dagger.Module
import dagger.hilt.InstallIn


import android.app.Application
import android.content.Context
import androidx.room.Room
import com.amorgens.wallet.data.AppDatabase
import com.amorgens.wallet.data.WalletService
import com.amorgens.wallet.domain.dao.WalletDAO
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context):AppDatabase{
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun getWalletDAO(appDatabase: AppDatabase):WalletDAO{
        return appDatabase.walletDAO()
    }

    @Provides
    fun getWalletService():WalletService{
        return WalletService()
    }
}