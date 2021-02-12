package test.skyscanner.common.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import test.skyscanner.common.entities.Countries
import test.skyscanner.common.entities.Currencies
import test.skyscanner.common.entities.Places
import test.skyscanner.common.entities.QuotesData

interface ApiService {
    @GET("reference/v1.0/countries/{locale}")
    suspend fun getCountries(
        @Path("locale") locale: String = "en-US",
        @Query("apikey") apikey: String = API_KEY,
    ): Countries

    @GET("reference/v1.0/currencies")
    suspend fun getCurrencies(
        @Query("apikey") apikey: String = API_KEY,
    ): Currencies

    @GET("autosuggest/v1.0/{country}/{currency}/{locale}")
    suspend fun getPlaces(
        @Path("country") country: String,
        @Path("currency") currency: String,
        @Path("locale") locale: String,
        @Query("query") query: String,
        @Query("apikey") apikey: String = API_KEY,
    ): Places

    @GET("browsequotes/v1.0/{country}/{currency}/{locale}/{originPlace}/{destinationPlace}/{outboundPartialDate}")
    suspend fun getQuotes(
        @Path("country") country: String,
        @Path("currency") currency: String,
        @Path("locale") locale: String,
        @Path("originPlace") originPlace: String,
        @Path("destinationPlace") destinationPlace: String,
        @Path("outboundPartialDate") outboundPartialDate: String,
        @Query("apikey") apikey: String = API_KEY,
    ): QuotesData

    companion object {
        const val API_KEY = "prtl6749387986743898559646983194"
    }
}
