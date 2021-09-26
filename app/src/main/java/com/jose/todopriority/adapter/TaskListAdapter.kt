package com.jose.todopriority.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jose.todopriority.R
import com.jose.todopriority.databinding.ItemTaksBinding
import com.jose.todopriority.model.Task

class TaskListAdapter: ListAdapter<Task, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var listenerEdit: (Task) -> Unit = {}
    var listenerDelete: (Task) -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListAdapter.TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaksBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskListAdapter.TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Task>?) {
        super.submitList(list?.let { ArrayList(it) })
    }


    inner class TaskViewHolder(
        private val binding: ItemTaksBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task){
            binding.tvTitle.text = item.title
            binding.tvDate.text = item.date
            binding.tvDescription.text = item.description
            binding.ivMore.setOnClickListener {
                showPopup(item)
            }
        }

        fun showPopup(item: Task) {
            val ivMore = binding.ivMore
            val popupMenu = PopupMenu(ivMore.context, ivMore)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_edit -> listenerEdit(item)
                    R.id.action_delete -> listenerDelete(item)

                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }
    }
}

class DiffCallback: DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        val isTitleTheSame = oldItem.title == newItem.title
        val isDateTheSame = oldItem.date == newItem.date
        val isHourTheSame = oldItem.hour == newItem.hour
        val isDescriptionTheSame = oldItem.description == newItem.description

        return isTitleTheSame && isDateTheSame && isHourTheSame && isDescriptionTheSame
    }

}