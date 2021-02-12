package test.skyscanner.common.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Places(
    @SerialName("Places")
    val places: List<Place>,
)

@Serializable
data class Place(
    @SerialName("PlaceId")
    val placeId: String = "",
    @SerialName("PlaceName")
    val placeName: String = "",
    @SerialName("CountryId")
    val countryId: String = "",
    @SerialName("RegionId")
    val regionId: String = "",
    @SerialName("CityId")
    val cityId: String = "",
    @SerialName("CountryName")
    val countryName: String = "",
) {
    override fun toString() = placeName
}


