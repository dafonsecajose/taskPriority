package com.jose.todopriority.ui

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.jose.todopriority.R
import com.jose.todopriority.application.TaskApplication
import com.jose.todopriority.databinding.ActivityAddTaskBinding
import com.jose.todopriority.extensions.format
import com.jose.todopriority.extensions.text
import com.jose.todopriority.model.Task
import java.util.*

class AddTaskActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    private lateinit var codePriority : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()


        if(intent.hasExtra(TASK_ID)) binding.btnNewTask.setText("Editar Tarefa")
        setupPrioritySpinner()
        setupTask()
        setupListerners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayListOf("Baixa", "Normal", "Alta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)

        binding.spnPriority.adapter = adapter

    }

    private fun setupTask(){
        if(intent.hasExtra(TASK_ID)){
            val taskId = intent.getIntExtra(TASK_ID, 0)
            var list = TaskApplication.instance.taskDB?.searchTasks("$taskId", true)
            var task = list?.getOrNull(0)
            binding.tilTitle.text = task?.title ?: ""
            binding.tilDate.text = task?.date ?: ""
            binding.tilHour.text = task?.hour ?: ""
            binding.tilDescription.text = task?.description ?: ""
            binding.spnPriority.setSelection(task?.priority?.toInt() ?: 0)
            codePriority = task?.priority.toString()
        }
    }

    private fun setupListerners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKE_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val minute = if (timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute
                val hour = if (timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour

                binding.tilHour.text = "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.btnNewTask.setOnClickListener {
            clearValidadeFied()
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                description = binding.tilDescription.text,
                priority = codePriority,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            if(validateField()){
                if(!intent.hasExtra(TASK_ID)){
                    TaskApplication.instance.taskDB?.saveTask(task)
                } else {
                    TaskApplication.instance.taskDB?.updateTask(task)
                }
                setResult(Activity.RESULT_OK)
                finish()
            }

        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.spnPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               codePriority = position.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun validateField() : Boolean {

        when {
            binding.tilTitle.text.isEmpty() -> {
                handleError(binding.tilTitle, "Informe o Titulo")
                return false
            }
            binding.tilDate.text.isEmpty() -> {
                handleError( binding.tilDate, "Informe a Data")
                return false
            }
            binding.tilHour.text.isEmpty() -> {
                handleError(binding.tilHour, "Informe a hora")
                return false
            }
            binding.tilDescription.text.isEmpty() -> {
                handleError( binding.tilDescription, "Informe a Descrição")
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun clearValidadeFied(){
        binding.tilTitle.error = null
        binding.tilDate.error = null
        binding.tilHour.error = null
        binding.tilDescription.error = null
    }

    private fun handleError(field : TextInputLayout, message: String){
        field.error = message
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home ->{
                finish()
                true;
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    companion object {
        const val TASK_ID = "task_id"
    }

}