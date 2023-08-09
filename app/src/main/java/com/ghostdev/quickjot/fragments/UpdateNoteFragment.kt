package com.ghostdev.quickjot.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.findNavController
import com.ghostdev.quickjot.MainActivity
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.databinding.FragmentUpdateNoteBinding
import com.ghostdev.quickjot.model.Note
import com.ghostdev.quickjot.viewmodel.NoteViewModel

class UpdateNoteFragment : Fragment() {
    private  var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var currentNote: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        val noteData = bundle!!.getParcelable<Note>("note")

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = noteData!!

        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)
        binding.etNoteBodyUpdate.setText(currentNote.noteBody)

        //IF USER UPDATES THE NOTES
        binding.fabDone.setOnClickListener {
            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()

            if (title.isNotEmpty()) {
                if (currentNote.lock) {
                    val note = Note(currentNote.id, title, body, true)
                    notesViewModel.updateNote(note)
                    view.findNavController().navigate(R.id.action_updateNoteFragment_to_lockedNoteFragment)
                } else {
                    val note = Note(currentNote.id, title, body, false)
                    notesViewModel.updateNote(note)
                    view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)
                }
            } else {
                Toast.makeText(context, "Title?", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Note")
            setMessage("This masterpiece will be deleted")
            setPositiveButton("Delete") {_, _ ->
                notesViewModel.deleteNote(currentNote)
                if (currentNote.lock) {
                    view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_lockedNoteFragment)
                } else {
                    view?.findNavController()?.navigate(R.id.action_updateNoteFragment_to_homeFragment)
                }
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_update_note, menu)
        val deleteMenuItem: MenuItem? = menu.findItem(R.id.menu_delete)
        deleteMenuItem?.setOnMenuItemClickListener {
            deleteNote()
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }

}