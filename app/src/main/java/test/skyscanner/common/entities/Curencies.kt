package test.skyscanner.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import test.skyscanner.common.db.currency.CurrencyEntity

@Serializable
class Currencies(
    @SerialName("Currencies")
    val currencies: List<Currency>,
)

@Serializable
@Parcelize
data class Currency(
    @SerialName("Code")
    val code: String = "",
    @SerialName("Symbol")
    val symbol: String = "",
    @SerialName("ThousandsSeparator")
    val thousandsSeparator: String = "",
    @SerialName("DecimalSeparator")
    val decimalSeparator: String = "",
    @SerialName("SymbolOnLeft")
    val symbolOnLeft: Boolean = false,
    @SerialName("SpaceBetweenAmountAndSymbol")
    val spaceBetweenAmountAndSymbol: Boolean = false,
    @SerialName("RoundingCoefficient")
    val roundingCoefficient: Long = 0L,
    @SerialName("DecimalDigits")
    val decimalDigits: Long = 0L,
) : Parcelable {

    fun toDbEntity() = CurrencyEntity(code, symbol, thousandsSeparator, decimalSeparator,
        symbolOnLeft, spaceBetweenAmountAndSymbol, roundingCoefficient, decimalDigits)

    override fun toString() = "$code - $symbol"
}