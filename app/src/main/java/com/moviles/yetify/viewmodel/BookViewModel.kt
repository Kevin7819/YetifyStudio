package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.moviles.yetify.datastore.UserPreferences
import com.moviles.yetify.models.Book
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application.applicationContext)

    // list of books
    private val _listBooks = MutableStateFlow<List<Book>>(emptyList())
    val listBooks: StateFlow<List<Book>> get() = _listBooks

    // single book
    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> get() = _book

    // loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    // error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    /**
     * Fetches all books and updates _listBooks.
     */
    fun fetchAllBooks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val userId = userPreferences.userId.firstOrNull()
                if (userId == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }

                // Llamada actualizada con userId
                val books = RetrofitInstance.api.getAllBooks(userId)
                _listBooks.value = books

            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching all books: ${e.message}", e)
                _errorMessage.value = "Error al cargar los libros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetches a single book by ID and updates _book.
     */
    fun getBookById(id: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val userId = userPreferences.userId.firstOrNull()
                if (userId == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }
                val bookById = RetrofitInstance.api.getBookById(id, userId)
                Log.i("viewmodel book ln 77","${bookById}")
                _book.value = bookById
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching book by ID $id: ${e.message}", e)
                _errorMessage.value = "Error al cargar el libro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Searches for books based on a query string and updates _listBooks.
     */
    fun getSearchBook(search: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val userId = userPreferences.userId.firstOrNull()
                if (userId == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }
                if (search.isNotBlank()) {
                    // Llamada actualizada con userId
                    val books = RetrofitInstance.api.searchBooks(search, userId)
                    _listBooks.value = books
                } else {
                    fetchAllBooks() // Reutiliza el método que ya tiene userId
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error searching books for '$search': ${e.message}", e)
                _errorMessage.value = "Error al buscar libros: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update progress
     */
    fun updateBookProgress(bookId: Int, progress: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val userId = userPreferences.userId.firstOrNull()
                if (userId == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }

                val progressBody = JsonObject().apply {
                    addProperty("progress", progress)
                }
                Log.i("viewmodelbook", "$progressBody")
                val response = RetrofitInstance.api.updateProgress(bookId, userId, progressBody)
                Log.i("viewmodel", "$response")
                if (response.isSuccessful) {
                   fetchAllBooks()

                    Log.d("BookViewModel", "Progress updated successfully for book $bookId")
                } else {
                    _errorMessage.value = "Error al actualizar progreso"
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error updating progress for book $bookId: ${e.message}", e)
                _errorMessage.value = "Error al actualizar progreso: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Resets single book (_book) to null
     */
    fun resetBook() {
        _book.value = null
    }

    /**
     * reset message error
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * get progress (0-100)
     */
    fun getProgressPercentage(progress: Double): Int {
        return (progress * 100).toInt()
    }

    /**
     * verify init book
     */
    fun isBookStarted(book: Book): Boolean {
        if (book?.progress ==null)
            return false
        return book.progress > 0.0
    }

    /**
     * verify complete book
     */
    fun isBookCompleted(book: Book): Boolean {
        if (book?.progress ==null)
            return false
        return book.progress >= 1.0
    }
}