package com.ifmg.gerenciadordetarefas

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val REQUEST_ADD_TASK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskAdapter = TaskAdapter()

        val recyclerViewTasks = findViewById<RecyclerView>(R.id.recyclerViewTasks)
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)

        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        recyclerViewTasks.adapter = taskAdapter

        fabAddTask.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), REQUEST_ADD_TASK)
        }

        // Configurando ouvintes para os botões de editar e excluir no adapter
        taskAdapter.setOnEditClickListener { task ->
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra("taskId", task.id) // Envie o ID da tarefa para edição
            startActivityForResult(intent, REQUEST_ADD_TASK)
        }

        taskAdapter.setOnDeleteClickListener { task ->
            TaskDBHelper(this).deleteTask(task.id)
            updateTaskList()
        }

        updateTaskList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_TASK && resultCode == Activity.RESULT_OK) {
            val taskAdded = data?.getBooleanExtra("taskAdded", false) ?: false
            if (taskAdded) {
                updateTaskList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateTaskList()
    }

    private fun updateTaskList() {
        val tasks = TaskDBHelper(this).getAllTasks()

        if (tasks.isEmpty()) {
            // Exibir mensagem "Você não tem nenhuma tarefa"
        } else {
            taskAdapter.setTasks(tasks)
        }
    }
}
