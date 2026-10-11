package au.edu.jcu.assessment.utilityapp.network

import retrofit2.http.GET

data class Quote(
    val id: Int = 0,
    val quote: String,
    val author: String
)

interface QuoteApiService {

    @GET("quotes/random")
    suspend fun getRandomQuote(): Quote
}
