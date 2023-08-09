package com.ghostdev.quickjot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ghostdev.quickjot.model.Note
import com.ghostdev.quickjot.repository.NotesRepository
import kotlinx.coroutines.launch

class NoteViewModel(app: Application, private val notesRepository: NotesRepository): AndroidViewModel(app) {


    fun addNote(note: Note) {
        viewModelScope.launch {
            notesRepository.insertNote(note)
        }
    }
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }
    fun updateNote(note: Note) {
        viewModelScope.launch {
            notesRepository.updateNote(note)
        }
    }

    fun getAllNotes() = notesRepository.getAllNotes()

    fun searchNotes(query: String?) = notesRepository.searchNotes(query)


}