package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import au.edu.jcu.assessment.utilityapp.network.Quote
import au.edu.jcu.assessment.utilityapp.network.QuoteApiService
import au.edu.jcu.assessment.utilityapp.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class QuoteUiState(
    val quote: String = "Small steps every day lead to big results.",
    val author: String = "StudyFlow",
    val isLoading: Boolean = false,
    val error: String? = null
)

class QuoteViewModel(
    private val repository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchQuote()
    }

    fun fetchQuote() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val result: Quote = repository.getRandomQuote()

                _uiState.value = QuoteUiState(
                    quote = result.quote,
                    author = result.author
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unable to load a new quote."
                )
            }
        }
    }

    companion object {
        private val apiService: QuoteApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(
                    GsonConverterFactory.create()
                )
                .build()
                .create(QuoteApiService::class.java)
        }

        private val repository: QuoteRepository by lazy {
            QuoteRepository(apiService)
        }

        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                require(
                    modelClass.isAssignableFrom(
                        QuoteViewModel::class.java
                    )
                ) { "Unknown ViewModel class: ${modelClass.name}" }
                return QuoteViewModel(repository) as T
            }
        }
    }
}
