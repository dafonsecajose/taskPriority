package com.jose.todopriority.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jose.todopriority.ui.adapter.TaskListAdapter
import com.jose.todopriority.core.extensions.createDialog
import com.jose.todopriority.core.extensions.createProgressDialog
import com.jose.todopriority.databinding.ActivityMainBinding
import com.jose.todopriority.presentation.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dialog by lazy { createProgressDialog() }
    private val adapter by lazy { TaskListAdapter() }
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateList()

        setupListeners()
        lifecycle.addObserver(viewModel)
    }

    private fun setupRecyclerView() {
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        adapter.listenerDelete = {
            viewModel.deleteTask(it)
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("task", it)
            startActivity(intent)
        }
    }

    private fun updateList() {
        viewModel.state.observe(this) {
            when (it) {
                MainViewModel.State.Loading -> dialog.show()
                is MainViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog{
                        setMessage(it.error.message)
                    }.show()
                }
                is MainViewModel.State.Success -> {
                    dialog.dismiss()
                    binding.includeEmpty.emptyState.visibility = if (it.list.isEmpty()) View.VISIBLE
                    else View.GONE
                    adapter.submitList(it.list)
                }
                MainViewModel.State.Deleted -> {
                    dialog.dismiss()
                }
            }
        }
    }
}