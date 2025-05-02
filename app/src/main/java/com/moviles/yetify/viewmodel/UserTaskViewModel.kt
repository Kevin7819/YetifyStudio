package com.moviles.yetify.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.yetify.models.UserTask
import com.moviles.yetify.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.HttpException

class UserTaskViewModel (application: Application) : AndroidViewModel(application) {

    private val _userTasks = MutableStateFlow<List<UserTask>>(emptyList())
    val userTasks: StateFlow<List<UserTask>> get() = _userTasks

    fun fetchUserTasks() {
        viewModelScope.launch {
            try {
                _userTasks.value = RetrofitInstance.api.getUserTasks()
                Log.i("MyViewModel", "Fetching data from API... ${_userTasks.value}")
            } catch (e: Exception) {
                Log.e("ViewmodelError", "Error: ${e}")
            }
        }
    }

    fun addUserTask (userTask: UserTask) {
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "UserTask: ${userTask}")
                val response = RetrofitInstance.api.addUserTask(userTask)
                _userTasks.value += response
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun updateUserTask(userTask: UserTask){
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "UserTask: ${userTask}")
                val response = RetrofitInstance.api.updateUserTask(userTask.id, userTask)
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