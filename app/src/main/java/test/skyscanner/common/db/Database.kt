package test.skyscanner.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import test.skyscanner.common.db.Database.Companion.VERSION
import test.skyscanner.common.db.countries.CountryDao
import test.skyscanner.common.db.countries.CountryEntity
import test.skyscanner.common.db.currency.CurrencyDao
import test.skyscanner.common.db.currency.CurrencyEntity

@Database(
    entities = [
        CurrencyEntity::class,
        CountryEntity::class,
    ],
    version = VERSION
)
abstract class Database : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun countriesDao(): CountryDao

    companion object {
        const val NAME = "skyscanner.db"
        const val VERSION = 1
    }
}