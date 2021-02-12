package test.skyscanner.common.db.countries

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface CountryDao {
    @Query("SELECT * FROM country")
    fun getAll(): List<CountryEntity>

    @Insert(onConflict = REPLACE)
    fun insert(entities: List<CountryEntity>)

    @Query("DELETE FROM country")
    fun deleteAll()
}