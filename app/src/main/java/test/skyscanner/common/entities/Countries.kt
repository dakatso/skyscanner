package test.skyscanner.common.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import test.skyscanner.common.db.countries.CountryEntity

@Serializable
class Countries(
    @SerialName("Countries")
    val countries: List<Country>,
)

@Serializable
data class Country(
    @SerialName("Code")
    val code: String = "",
    @SerialName("Name")
    val name: String = "",
) {
    fun toDbEntity() = CountryEntity(code, name)

    override fun toString() = name
}