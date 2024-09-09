package com.amorgens.general.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amorgens.wallet.domain.Wallet
import kotlinx.coroutines.flow.Flow


@Dao
interface AppDataDAO {
    @Query("SELECT * FROM wallet")
    fun getAllAppData() : Flow<List<Wallet>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWa(wallet : Wallet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWallet(wallet: Wallet)

    @Delete
    suspend fun deleteWallet(wallet : Wallet)
}

