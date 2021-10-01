package com.jose.todopriority.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.loader.app.LoaderManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jose.todopriority.R
import com.jose.todopriority.adapter.TaskListAdapter
import com.jose.todopriority.application.TaskApplication
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
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK) updateList()
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
                    adapter.submitList(it.list)
                    if (it.list.isEmpty()){
                        binding.includeEmpty
                    }
                }
            }
        }


    }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }
}