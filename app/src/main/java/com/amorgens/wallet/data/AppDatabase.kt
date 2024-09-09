package com.amorgens.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amorgens.general.data.Converters
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.dao.WalletDAO


@Database(entities = [Wallet::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract  class AppDatabase:RoomDatabase(){
    abstract fun walletDAO():WalletDAO

    companion object{
        @Volatile
        private var INSTANCE:AppDatabase? = null

        fun getDatabase(context:Context): AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance =Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(MIGRATION1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

val MIGRATION1_2 = object: Migration(1,2){
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE wallet ADD COLUMN userName TEXT NULL")
    }
}