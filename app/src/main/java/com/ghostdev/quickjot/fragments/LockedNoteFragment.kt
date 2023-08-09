package com.ghostdev.quickjot.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ghostdev.quickjot.MainActivity
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.adapter.LockedNoteAdapter
import com.ghostdev.quickjot.databinding.FragmentLockedNoteBinding
import com.ghostdev.quickjot.model.Note
import com.ghostdev.quickjot.viewmodel.NoteViewModel

class LockedNoteFragment : Fragment(R.layout.fragment_locked_note), SearchView.OnQueryTextListener {
    private  var _binding: FragmentLockedNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: LockedNoteAdapter
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLockedNoteBinding.inflate(inflater, container, false)
        // In your Activity or Application class
        sharedPreferences = requireContext().getSharedPreferences("jot", Context.MODE_PRIVATE)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel

        setUpRecyclerView()
        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_lockedNoteFragment_to_lockedNewNote)
        }
    }

    private fun setUpRecyclerView() {
            noteAdapter = LockedNoteAdapter()

            binding.recyclerView.apply {
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                setHasFixedSize(true)
                adapter = noteAdapter
            }

            activity.let {
                notesViewModel.getAllNotes().observe(
                    viewLifecycleOwner
                ) { note ->
                    noteAdapter.setNotes(note) // Use setNotes here to filter the notes
                    updateUI(note)
                }
            }
    }

    private fun updateUI(note: List<Note>?) {
        if (note.isNullOrEmpty()) {
            binding.cardView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.cardView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
//        searchNote(query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            // If the query is empty or null, show all notes by loading the complete list again
            notesViewModel.getAllNotes().observe(viewLifecycleOwner) { noteList ->
                noteAdapter.differ.submitList(noteList)
                updateUI(noteList)
            }
        } else {
            // Perform the search for non-empty query
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        val searchQuery = "%$query" // Make sure you use the correct query format here

        // Retrieve only unlocked notes that match the search query
        notesViewModel.searchNotes(searchQuery).observe(
            this
        ) { list ->
            val unlockedNotes = list.filter { note -> note.lock }
            noteAdapter.differ.submitList(unlockedNotes)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }
}