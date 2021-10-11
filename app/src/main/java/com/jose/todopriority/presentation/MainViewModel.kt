package com.jose.todopriority.presentation

import androidx.lifecycle.*
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.domain.DeleteTaskUseCase
import com.jose.todopriority.domain.ListTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class MainViewModel(
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val listTaskUseCase: ListTaskUseCase
): ViewModel(), LifecycleObserver {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun getTasks() {
        viewModelScope.launch {
            listTaskUseCase()
                .flowOn(Dispatchers.Main)
                .onStart {
                    _state.value = State.Loading
                }
                .catch {
                    _state.value = State.Error(it)
                }
                .collect {
                    _state.value = State.Success(it)
                }
        }
    }

    fun deleteTask(task: Task){
        viewModelScope.launch {
            deleteTaskUseCase(task)
                .flowOn(Dispatchers.Main)
                .onStart { _state.value = State.Loading }
                .catch { _state.value = State.Error(it) }
                .collect { _state.value = State.Deleted }

        }
    }


    sealed class State {
        object Loading: State()
        object Deleted: State()

        data class Success(val list: List<Task>): State()
        data class Error(val error: Throwable): State()
    }
}