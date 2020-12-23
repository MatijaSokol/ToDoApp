package hr.ferit.matijasokol.todoapp.ui.deleteAllCompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.todoapp.R

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message))
            .setNegativeButton(getString(R.string.dialog_negative_message), null)
            .setPositiveButton(getString(R.string.dialog_positive_message)) { _, _ ->
                viewModel.onConfirmClick()
            }.create()
}