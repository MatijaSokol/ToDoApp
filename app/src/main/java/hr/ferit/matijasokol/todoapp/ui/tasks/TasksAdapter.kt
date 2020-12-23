package hr.ferit.matijasokol.todoapp.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.ferit.matijasokol.todoapp.data.Task
import hr.ferit.matijasokol.todoapp.databinding.ItemTaskBinding

class TasksAdapter(
    private val onItemClicked: (Task) -> Unit,
    private val onCheckBoxClicked: (Task, Boolean) -> Unit
) : ListAdapter<Task, TasksAdapter.TasksViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Task>() {

            override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TasksViewHolder(
        ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) = holder.bind(getItem(position))

    inner class TasksViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            setListeners()
        }

        fun bind(task: Task) {
            with(binding) {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }

        private fun setListeners() {
            with(binding) {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClicked(getItem(position))
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onCheckBoxClicked(getItem(position), checkBoxCompleted.isChecked)
                    }
                }
            }
        }
    }
}