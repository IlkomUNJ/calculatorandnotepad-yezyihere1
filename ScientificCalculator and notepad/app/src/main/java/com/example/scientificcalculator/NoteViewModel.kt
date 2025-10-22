package com.example.scientificcalculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao by lazy {
        NoteDatabase.getDatabase(getApplication()).noteDao()
    }


    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    fun saveNote(id: Int, title: String, content: String) {
        viewModelScope.launch {
            val note = if (id == 0) {
                Note(title = title, content = content)
            } else {
                Note(id = id, title = title, content = content)
            }
            noteDao.insertOrUpdate(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }
}
