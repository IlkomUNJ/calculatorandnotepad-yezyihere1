package com.example.scientificcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.room.Entity
import androidx.room.PrimaryKey




enum class Screen {
    NoteList,
    NoteEditor
}

class NotepadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NotepadApp()
            }
        }
    }
}

@Composable
fun NotepadApp(noteViewModel: NoteViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(Screen.NoteList) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }

    when (currentScreen) {
        Screen.NoteList -> {
            NoteListScreen(
                noteViewModel = noteViewModel,
                onAddNote = {
                    noteToEdit = null
                    currentScreen = Screen.NoteEditor
                },
                onNoteClick = { note ->
                    noteToEdit = note
                    currentScreen = Screen.NoteEditor
                }
            )
        }
        Screen.NoteEditor -> {
            NoteEditorScreen(
                noteViewModel = noteViewModel,
                noteToEdit = noteToEdit,
                onBack = { currentScreen = Screen.NoteList }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    noteViewModel: NoteViewModel,
    onAddNote: () -> Unit,
    onNoteClick: (Note) -> Unit
) {
    val notes by noteViewModel.getAllNotes().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Semua Catatan") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNote) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Catatan")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(note = note, onClick = { onNoteClick(note) })
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.content, maxLines = 2)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteViewModel: NoteViewModel,
    noteToEdit: Note?,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(noteToEdit?.title ?: "") }
    var content by remember { mutableStateOf(noteToEdit?.content ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteToEdit == null) "Catatan Baru" else "Edit Catatan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        noteViewModel.saveNote(
                            id = noteToEdit?.id ?: 0,
                            title = title,
                            content = content
                        )
                        onBack()
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Simpan")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi catatan...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

private fun NoteViewModel.saveNote(id: Int, title: String, content: String) {}
