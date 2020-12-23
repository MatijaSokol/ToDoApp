package hr.ferit.matijasokol.todoapp.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.todoapp.R
import hr.ferit.matijasokol.todoapp.databinding.FragmentAddEditTaskBinding
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.ADD_EDIT_REQUEST_KEY
import hr.ferit.matijasokol.todoapp.util.exhaustive
import hr.ferit.matijasokol.todoapp.util.snackbar
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddEditTaskBinding.bind(view)

        setViewsData()
        setListeners()
        subscribeToObservers()
    }

    private fun setViewsData() {
        with(binding) {
            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = getString(R.string.created, viewModel.task?.createdDateFormatted)
        }
    }

    private fun subscribeToObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        snackbar(getString(event.messageResource), Snackbar.LENGTH_LONG)
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                            ADD_EDIT_REQUEST_KEY,
                            bundleOf(
                                ADD_EDIT_REQUEST_KEY to event.result
                            )
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}