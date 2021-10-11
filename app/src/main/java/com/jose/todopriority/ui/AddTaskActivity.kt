package com.jose.todopriority.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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
import com.jose.todopriority.presentation.AddTaskViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddTaskActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private val viewModel by viewModel<AddTaskViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private lateinit var codePriority : String
    private lateinit var  taskEdit: Task

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()


        if(intent.hasExtra(TASK)){
            binding.btnNewTask.text = "Editar Tarefa"
            taskEdit = intent.getSerializableExtra(TASK) as Task
            setupTask(taskEdit)
        }
        setupPrioritySpinner()
        setupObserver()
        setupListerners()
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
            codePriority = task.priority
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

        binding.spnPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               codePriority = position.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

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
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

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
                    createDialog { setMessage("Tarefa salva com sucesso") }.show()
                    this.finish()
                }
                is AddTaskViewModel.State.Success -> {
                    dialog.dismiss()
                }
                AddTaskViewModel.State.Updated ->{
                    dialog.dismiss()
                    this.finish()
                }

            }
        }
    }

    companion object {
        const val TASK = "task"
    }

}