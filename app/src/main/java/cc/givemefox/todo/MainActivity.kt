package cc.givemefox.todo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cc.givemefox.todo.ui.theme.TodoTheme

data class TodoItem(
    val id: Int,
    var title: String,
    var description: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                TodoApp()
            }
        }
    }
}

@Composable
fun TodoApp() {
    var todos by remember { mutableStateOf(listOf<TodoItem>()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTodo by remember { mutableStateOf<TodoItem?>(null) }
    val context = LocalContext.current

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { showAddDialog = true }) {
            Icon(Icons.Default.Add, contentDescription = "Add Todo")
        }
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)
            ) {
                items(todos) { todo ->
                    TodoItemCard(todo = todo, onClick = { selectedTodo = todo })
                }
            }
        }

        if (showAddDialog) {
            AddTodoDialog(onDismiss = { showAddDialog = false }, onAdd = { title, description ->
                todos = todos + TodoItem(
                    id = todos.size, title = title, description = description
                )
                showAddDialog = false
                Toast.makeText(context, "Task created: $title", Toast.LENGTH_SHORT).show()
            })
        }

        selectedTodo?.let { todo ->
            TodoDetailDialog(todo = todo,
                onDismiss = { selectedTodo = null },
                onEdit = { editedTitle, editedDescription ->
                    todos = todos.map {
                        if (it.id == todo.id) {
                            it.copy(title = editedTitle, description = editedDescription)
                        } else it
                    }
                    selectedTodo = null
                })
        }
    }
}

@Composable
fun TodoItemCard(todo: TodoItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = todo.title, style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = todo.description, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}