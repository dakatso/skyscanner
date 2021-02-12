package test.skyscanner.common.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class QuotesData(
    @SerialName("Quotes")
    val quotes: List<Quote>,
    @SerialName("Carriers")
    val carriers: List<Carrier>,
    @SerialName("Currencies")
    val currencies: List<Currency>,
) : Parcelable

@Serializable
@Parcelize
data class Quote(
    @SerialName("QuoteId")
    val quoteId: Int,
    @SerialName("MinPrice")
    val minPrice: Int,
    @SerialName("Direct")
    val direct: Boolean,
    @SerialName("OutboundLeg")
    val outboundLeg: OutboundLeg,
    @SerialName("QuoteDateTime")
    val quoteDateTime: String,
) : Parcelable

@Serializable
@Parcelize
data class OutboundLeg(
    @SerialName("CarrierIds")
    val carrierIds: List<Int>,
    @SerialName("OriginId")
    val originId: Int,
    @SerialName("DestinationId")
    val destinationId: Int,
    @SerialName("DepartureDate")
    val departureDate: String,
) : Parcelable

@Serializable
@Parcelize
data class Carrier(
    @SerialName("CarrierId")
    val carrierId: Int,
    @SerialName("Name")
    val name: String,
) : Parcelable

