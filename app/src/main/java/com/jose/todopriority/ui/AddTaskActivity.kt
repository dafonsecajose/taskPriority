package com.jose.todopriority.ui


import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.jose.todopriority.core.extensions.createDialog
import com.jose.todopriority.core.extensions.createProgressDialog
import com.jose.todopriority.databinding.ActivityAddTaskBinding
import com.jose.todopriority.core.extensions.format
import com.jose.todopriority.core.extensions.text
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.job.NotificationUtil
import com.jose.todopriority.job.NotificationWorkManager
import com.jose.todopriority.presentation.AddTaskViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class AddTaskActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val viewModel by viewModel<AddTaskViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private var taskIdEdit: Long? = null
    private val workManager = WorkManager.getInstance(application)
    private lateinit var task: Task
    private lateinit var taskEdit: Task
    private var notificationTaskId: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupPrioritySpinner()
        setupObserver()
        setupListeners()

        if(intent.hasExtra(TASK)){
            binding.btnNewTask.text = "Editar Tarefa"
            taskIdEdit = intent.getLongExtra(TASK, 0)
            viewModel.findTaskById(taskIdEdit!!)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupPrioritySpinner() {
        val priorities = arrayListOf("Baixa", "Normal", "Alta")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)

        binding.spnPriority.adapter = adapter

    }

    private fun setupTask(task: Task){
            binding.tilTitle.text = task.title
            binding.tilDate.text = task.date
            binding.tilHour.text = task.hour
            binding.tilDescription.text = task.description
            binding.spnPriority.setSelection(task.priority.toInt())
    }

    private fun setupListeners() {
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
            clearValidateField()
            task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                description = binding.tilDescription.text,
                priority = binding.spnPriority.selectedItemPosition.toString(),
                id = if (intent.hasExtra(TASK)) taskEdit.id else 0
            )
            if(validateField()){
                if(!intent.hasExtra(TASK)){
                    viewModel.saveTask(task)

                } else {
                    viewModel.updateTask(task)
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            finish()
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

    private fun clearValidateField(){
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
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserver() {
        viewModel.state.observe(this){
            when (it) {
                is AddTaskViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                AddTaskViewModel.State.Loading -> dialog.show()
                AddTaskViewModel.State.Saved -> {
                    dialog.dismiss()
                    if (notificationTaskId != null) {
                            createWorkManager(task)
                    }
                    Toast.makeText(this@AddTaskActivity, "Tarefa agendada com sucesso!",
                        Toast.LENGTH_LONG).show()
                    this.finish()
                }
                is AddTaskViewModel.State.Success -> {
                    taskEdit = it.task
                    setupTask(taskEdit)
                    dialog.dismiss()
                }
                AddTaskViewModel.State.Updated ->{
                    notificationTaskId = taskEdit.id
                    if (notificationTaskId != null) {
                        cancelNotification()
                        createWorkManager(task)
                    }
                    dialog.dismiss()
                    this.finish()
                }
            }
        }

        viewModel.taskId.observe(this, androidx.lifecycle.Observer {
            notificationTaskId = it
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createWorkManager(task: Task) {
        //calculate item
        val timeString = "${task.date} ${task.hour}"
        val dateTime = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val timeFuture = dateTime - System.currentTimeMillis()

        val data = Data.Builder()
        data.putString(EXTRA_TASK_TITLE, task.title)
        data.putLong(EXTRA_TASK_ID, notificationTaskId!!)

        val workRequest = OneTimeWorkRequest.Builder(NotificationWorkManager::class.java)
            .setInitialDelay(timeFuture, TimeUnit.MILLISECONDS)
            .setInputData(data.build())
            .addTag(notificationTaskId.toString())
            .build()
        workManager.enqueue(workRequest)
    }

    private fun cancelNotification() {
        val notificationUtil = NotificationUtil
        notificationUtil.deleteNotification(application, notificationTaskId?.toInt()!!)
    }

    companion object {
        const val SCHEDULE_EXTRA_TASK_TITLE = "SCHEDULE_EXTRA_TASK_TITLE"
        const val EXTRA_TASK_TITLE = "EXTRA_TASK_TITLE"
        const val EXTRA_TASK_ID = "EXTRA_TASK_ID"
        const val TASK = "taskId"
    }

}