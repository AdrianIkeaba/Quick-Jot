package com.ghostdev.quickjot.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ghostdev.quickjot.MainActivity
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.databinding.FragmentLockedNewNoteBinding
import com.ghostdev.quickjot.model.Note
import com.ghostdev.quickjot.viewmodel.NoteViewModel

class LockedNewNoteFragment : Fragment(R.layout.fragment_locked_new_note) {

    private var _binding: FragmentLockedNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var mView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLockedNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view

    }

    private fun saveNote(view: View) {
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()

        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteBody, true)

            notesViewModel.addNote(note)
            Toast.makeText(context, "Jot saved", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.action_lockedNewNote_to_lockedNoteFragment)
        } else {
            Toast.makeText(context, "Title is empty", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_note, menu)

        // Get the menu item
        val menuItem = menu.findItem(R.id.menu_save)

        // Get the desired color
        val color = ContextCompat.getColor(requireContext(), R.color.white)

        // Create a SpannableString with the menu item title
        val spannableString = SpannableString(menuItem.title)

        // Apply the ForegroundColorSpan to change the text color
        spannableString.setSpan(ForegroundColorSpan(color), 0, spannableString.length, 0)

        // Set the modified SpannableString as the title of the menu item
        menuItem.title = spannableString

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                saveNote(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }
}