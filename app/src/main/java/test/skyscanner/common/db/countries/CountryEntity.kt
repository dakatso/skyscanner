package test.skyscanner.common.db.countries

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import test.skyscanner.common.entities.Country

@Entity(tableName = "country")
data class CountryEntity(
    @ColumnInfo(name = "code")
    val code: String = "",
    @ColumnInfo(name = "name")
    val name: String = "",
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var id: Long = 0

    fun toDto() = Country(code, name)
}