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
import com.jose.todopriority.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateList()

        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }

        adapter.listenerDelete = {
            TaskApplication.instance.taskDB?.deleteTask(it.id)
            updateList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK) updateList()
    }

    private fun updateList() {
        val list = TaskApplication.instance.taskDB?.searchTasks("")
        binding.includeEmpty.emptyState.visibility = if (list?.isEmpty() == true) View.VISIBLE
        else View.GONE
        adapter.submitList(list)


    }

    companion object {
        private const val CREATE_NEW_TASK = 1000
    }
}