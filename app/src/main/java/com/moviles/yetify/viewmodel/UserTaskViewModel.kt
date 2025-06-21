package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.datastore.UserPreferences
import com.moviles.yetify.models.Course
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

class UserTaskViewModel (application: Application) : AndroidViewModel(application) {

    private val _userTasks = MutableStateFlow<List<UserTask>>(emptyList())
    val userTasks: StateFlow<List<UserTask>> get() = _userTasks

    private val userPreferences = UserPreferences(application.applicationContext)

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value = RetrofitInstance.api.getCourses()
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error fetching courses: ${e.message}")
            }
        }
    }

//    fun fetchUserTasks() {
//        viewModelScope.launch {
//            try {
//                _userTasks.value = RetrofitInstance.api.getUserTasks()
//                Log.i("MyViewModel", "Fetching data from API... ${_userTasks.value}")
//            } catch (e: Exception) {
//                Log.e("ViewmodelError", "Error: ${e}")
//            }
//        }
//    }

    fun fetchUserTasks() {
        viewModelScope.launch {
            try {

                val userId = userPreferences.userId.firstOrNull()
                Log.i("MyViewModel", "userId = $userId")
                if (userId != null) {

                    _userTasks.value = RetrofitInstance.api.getUserTasksByUserId(userId)
                    Log.i("MyViewModel", "Fetching data for user $userId: ${_userTasks.value}")
                } else {
                    Log.e("ViewmodelError", "User ID not found")
                }
            } catch (e: Exception) {
                Log.e("ViewmodelError", "Error: ${e}")
            }
        }
    }

//    fun addUserTask (userTask: UserTask) {
//        viewModelScope.launch {
//            try {
//
//                Log.i("ViewModelInfo", "UserTask: ${userTask}")
//                val response = RetrofitInstance.api.addUserTask(userTask)
//                _userTasks.value += response
//                Log.i("ViewModelInfo", "Response: ${response}")
//            } catch (e: HttpException) {
//                val errorBody = e.response()?.errorBody()?.string()
//                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
//            } catch (e: Exception) {
//                Log.e("ViewModelError", "Error: ${e.message}", e)
//            }
//        }
//    }

    fun addUserTask(userTask: UserTask, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = userPreferences.userId.firstOrNull()
                if (userId != null) {
                    Log.i("ViewModelInfo", "Creating task for userId: $userId with data: $userTask")

                    val response = RetrofitInstance.api.createTaskByUser(userId, userTask)

                    if (response.isSuccessful) {
                        response.body()?.let { createdTask ->
                            _userTasks.value = _userTasks.value + createdTask
                            onSuccess()
                        } ?: run {
                            onError("El servidor no devolvió datos")
                        }
                    } else {
                        val errorMsg = try {
                            "Error ${response.code()}: ${response.errorBody()?.string() ?: "Sin detalles"}"
                        } catch (e: Exception) {
                            "Error al leer mensaje de error"
                        }
                        onError(errorMsg)
                    }
                } else {
                    onError("Usuario no autenticado")
                }
            } catch (e: CancellationException) {
                Log.w("ViewModel", "Operación cancelada", e)
                onError("Operación cancelada")
            } catch (e: Exception) {
                Log.e("ViewModel", "Error inesperado", e)
                onError("Error inesperado: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getCurrentUserId(): Int {
        return userPreferences.userId.first() ?: 0
    }

    fun updateUserTask(userTask: UserTask){
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo 135", "UserTask: ${userTask}")
                if (userTask.id == null) {
                    throw IllegalArgumentException("El ID del UserTask no puede ser nulo")
                }
                val response = RetrofitInstance.api.updateUserTask(userTask?.id, userTask)
                _userTasks.value = _userTasks.value.map { userTask ->
                    if (userTask.id == response.id) response else userTask
                }
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun deleteUserTask(userTaskId: Int?) {
        userTaskId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteUserTask(id)
                    _userTasks.value = _userTasks.value.filter { it.id != userTaskId }
                } catch (e: Exception) {
                    Log.e("ViewModelError", "Error deleting event: ${e.message}")
                }
            }
        } ?: Log.e("ViewModelError", "Error: userTaskId is null")
    }

    data class PartsEvent(
        val idUser: RequestBody,
        val idCourse: RequestBody,
        val description: RequestBody,
        val dueDate: RequestBody,
        val status: RequestBody
    )
}