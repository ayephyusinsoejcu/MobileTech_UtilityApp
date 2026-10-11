package au.edu.jcu.assessment.utilityapp.repository

import au.edu.jcu.assessment.utilityapp.network.Quote
import au.edu.jcu.assessment.utilityapp.network.QuoteApiService

class QuoteRepository(
    private val apiService: QuoteApiService
) {
    suspend fun getRandomQuote(): Quote {
        return apiService.getRandomQuote()
    }
}
