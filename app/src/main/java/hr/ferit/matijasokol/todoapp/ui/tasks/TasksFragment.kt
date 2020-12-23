package hr.ferit.matijasokol.todoapp.ui.tasks

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.todoapp.R
import hr.ferit.matijasokol.todoapp.data.SortOrder
import hr.ferit.matijasokol.todoapp.data.Task
import hr.ferit.matijasokol.todoapp.databinding.FragmentTasksBinding
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.ADD_EDIT_REQUEST_KEY
import hr.ferit.matijasokol.todoapp.util.exhaustive
import hr.ferit.matijasokol.todoapp.util.onQueryTextChanged
import hr.ferit.matijasokol.todoapp.util.snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()
    private val tasksAdapter by lazy { TasksAdapter(
        onItemClicked =  { onItemClicked(it) },
        onCheckBoxClicked = { task, isChecked -> onCheckBoxClicked(task, isChecked) }
    ) }

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTasksBinding.bind(view)
        setHasOptionsMenu(true)

        initRecycler()
        setListeners()
        subscribeToObservers()

        setFragmentResultListener(ADD_EDIT_REQUEST_KEY) { _, bundle ->
            val result = bundle.getInt(ADD_EDIT_REQUEST_KEY)
            viewModel.onAddEditResult(result)
        }
    }

    private fun initRecycler() {
        binding.recyclerViewTasks.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            ItemTouchHelper(SwipeTouchHelper { viewModel.onTaskSwiped(tasksAdapter.currentList[it]) })
                .attachToRecyclerView(this)
        }
    }

    private fun setListeners() {
        binding.fabAddTask.setOnClickListener {
            viewModel.onAddNewTaskClick()
        }
    }

    private fun onCheckBoxClicked(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckChanged(task, isChecked)
    }

    private fun onItemClicked(task: Task) {
        viewModel.onTaskSelected(task)
    }

    private fun subscribeToObservers() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        snackbar(getString(R.string.task_deleted), Snackbar.LENGTH_LONG, getString(R.string.undo)) {
                            viewModel.onUndoDeleteClick(event.task)
                        }
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        snackbar(getString(event.messageResource))
                    }
                    is TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(null, getString(R.string.new_task))
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment(event.task, getString(R.string.edit_task))
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToDeleteAllCompletedScreen -> {
                        val action = TasksFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tasks, menu)
        initSearchView(menu)

        lifecycleScope.launch {
            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }
    }

    private fun initSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        val pendingQuery = viewModel.searchQuery.value
        if (!pendingQuery.isNullOrEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.onHideCompletedClicked(item.isChecked)
                true
            }
            R.id.action_delete_all_completed_tasks -> {
                viewModel.onDeleteAllCompletedTasks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
        _binding = null
    }
}