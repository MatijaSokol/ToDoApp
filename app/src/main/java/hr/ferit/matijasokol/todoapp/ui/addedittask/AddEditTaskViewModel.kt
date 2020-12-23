package hr.ferit.matijasokol.todoapp.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.todoapp.R
import hr.ferit.matijasokol.todoapp.data.Task
import hr.ferit.matijasokol.todoapp.data.TaskDao
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.TASK
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.TASK_IMPORTANCE
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.TASK_NAME
import hr.ferit.matijasokol.todoapp.ui.ADD_TASK_RESULT_OK
import hr.ferit.matijasokol.todoapp.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>(TASK)

    var taskName = state.get<String>(TASK_NAME) ?: task?.name ?: ""
        set(value) {
            field = value
            state.set(TASK_NAME, value)
        }

    var taskImportance = state.get<Boolean>(TASK_IMPORTANCE) ?: task?.important ?: false
        set(value) {
            field = value
            state.set(TASK_IMPORTANCE, value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage(R.string.empty_name)
            return
        }

        if (task != null) {
            val updateTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updateTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(messageResource: Int) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(messageResource))
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val messageResource: Int) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}