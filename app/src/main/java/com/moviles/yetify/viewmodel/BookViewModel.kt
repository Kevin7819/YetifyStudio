package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.models.Book
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class BookViewModel(application: Application) : AndroidViewModel(application) {

    // list of books
    private val _listBooks = MutableStateFlow<List<Book>>(emptyList())
    val listBooks: StateFlow<List<Book>> get() = _listBooks

    // single book
    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> get() = _book

    /**
     * Fetches all books and updates _listBooks.
     */
    fun fetchAllBooks() {
        viewModelScope.launch {
            try {
                // get list books
                val books = RetrofitInstance.api.getListBooks()
                _listBooks.value = books
            } catch (e: Exception) {
                // Log the error for debugging
                Log.e("BookViewModel", "Error fetching all books: ${e.message}", e)
            }
        }
    }

    /**
     * Fetches a single book by ID and updates _book.
     */
    fun getBookById(id: Int) {
        viewModelScope.launch {
            try {
                val bookById = RetrofitInstance.api.getBook(id)
                _book.value = bookById
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error fetching book by ID $id: ${e.message}", e)
            }
        }
    }

    /**
     * Searches for books based on a query string and updates _listBooks.
     */
    fun getSearchBook(search: String) {
        viewModelScope.launch {
            try {
                if (search.isNotBlank()) {
                    val books = RetrofitInstance.api.getSearchBook(search)
                    _listBooks.value = books
                }else {
                    fetchAllBooks()
                }
            } catch (e: Exception) {
                Log.e("BookViewModel", "Error searching books for '$search': ${e.message}", e)
            }
        }
    }

    /**
     * Resets single book (_book) to null
     */
    fun resetBook() {
        _book.value = null // This is how you actually reset the StateFlow
    }
}