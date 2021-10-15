package com.jose.todopriority.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.domain.ListTaskUseCase
import com.jose.todopriority.domain.NewSaveTaskUseCase
import com.jose.todopriority.domain.SaveTaskUseCase
import com.jose.todopriority.domain.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class AddTaskViewModel(
    private val newSaveTaskUseCase: NewSaveTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
): ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state
    private val _taskId = MutableLiveData<Long>()
    val taskId: LiveData<Long> = _taskId

    fun saveTask(task: Task) {
        viewModelScope.launch {
            newSaveTaskUseCase(task)
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect{
                    _taskId.value = it
                    _state.value = State.Saved
                }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Updated
                }
        }
    }

    sealed class State {
        object Loading: State()
        object Saved: State()
        object Updated: State()

        data class Success(val task: Task): State()
        data class Error(val error: Throwable): State()
    }

}