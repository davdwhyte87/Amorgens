package com.amorgens.wallet.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.WalletC
import kotlinx.coroutines.flow.Flow


@Dao
interface WalletDAO{
    @Query("SELECT * FROM wallet")
    fun getAllWallet() : Flow<List<Wallet>>
    @Query("SELECT * FROM wallet WHERE userName = :uname")
    fun getAllWallet2(uname:String) : Flow<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallet(wallet : Wallet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWallet(wallet:Wallet)

    @Delete
    suspend fun deleteWallet(wallet : Wallet)
}