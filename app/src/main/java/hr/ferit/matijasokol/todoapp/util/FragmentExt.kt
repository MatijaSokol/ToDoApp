package hr.ferit.matijasokol.todoapp.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.snackbar(
    message: String,
    length: Int = Snackbar.LENGTH_SHORT,
    actionMessage: String? = null,
    action: (() -> Unit)? = null
)  {
    Snackbar.make(requireView(), message, length).apply {
        if (actionMessage != null && action != null) {
            setAction(actionMessage) { action() }
        }
    }.show()
}