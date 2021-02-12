package test.skyscanner.common.db.currency

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency")
    fun getAll(): List<CurrencyEntity>

    @Insert(onConflict = REPLACE)
    fun insert(entities: List<CurrencyEntity>)

    @Query("DELETE FROM currency")
    fun deleteAll()
}