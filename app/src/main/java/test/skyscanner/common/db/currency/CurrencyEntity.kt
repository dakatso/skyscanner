package test.skyscanner.common.db.currency

import android.provider.BaseColumns
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import test.skyscanner.common.entities.Currency

@Entity(tableName = "currency")
data class CurrencyEntity(
    @ColumnInfo(name = "code")
    val code: String = "",
    @ColumnInfo(name = "symbol")
    val symbol: String = "",
    @ColumnInfo(name = "thousands_separator")
    val thousandsSeparator: String = "",
    @ColumnInfo(name = "decimal_separator")
    val decimalSeparator: String = "",
    @ColumnInfo(name = "symbol_on_left")
    val symbolOnLeft: Boolean = false,
    @ColumnInfo(name = "space_between_amount_and_symbol")
    val spaceBetweenAmountAndSymbol: Boolean = false,
    @ColumnInfo(name = "rounding_coefficient")
    val roundingCoefficient: Long = 0L,
    @ColumnInfo(name = "decimal_digits")
    val decimalDigits: Long = 0L,

    ) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseColumns._ID)
    var id: Long = 0

    fun toDto() = Currency(code, symbol, thousandsSeparator, decimalSeparator,
        symbolOnLeft, spaceBetweenAmountAndSymbol, roundingCoefficient, decimalDigits)
}