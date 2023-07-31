package com.ghostdev.quickjot.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ghostdev.quickjot.repository.NotesRepository

class NoteViewModelFactory(val app: Application, private val notesRepository: NotesRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(app, notesRepository) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}