package com.ghostdev.quickjot.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.adapter.NoteAdapter
import com.ghostdev.quickjot.databinding.FragmentHomeBinding
import com.ghostdev.quickjot.model.Note
import com.ghostdev.quickjot.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = ViewModelProvider(requireActivity()).get(NoteViewModel::class.java)

        setUpRecyclerView()

        binding.fabAddNote.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }

        updateUI(emptyList())
    }

    private fun setUpRecyclerView() {
        noteAdapter = NoteAdapter()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        notesViewModel.getAllNotes().observe(viewLifecycleOwner) { noteList ->
            noteAdapter.setNotes(noteList)
            updateUI(noteList)
        }
    }

    private fun updateUI(noteList: List<Note>) {
        val hasUnlockedNotes = noteList.any { !it.lock }
        binding.cardView.visibility = if (hasUnlockedNotes) View.GONE else View.VISIBLE
        binding.recyclerView.visibility = if (hasUnlockedNotes) View.VISIBLE else View.GONE
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
                val unlockedNotes = noteList.filter { note -> !note.lock }
                noteAdapter.differ.submitList(unlockedNotes)
                updateUI(unlockedNotes)
            }
        } else {
            // Perform the search for non-empty query
            searchNote(newText)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        } else {
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
        }
    }
    private fun searchNote(query: String?) {
        val searchQuery = "%$query" // Make sure you use the correct query format here

        // Retrieve only unlocked notes that match the search query
        notesViewModel.searchNotes(searchQuery).observe(
            this
        ) { list ->
            val unlockedNotes = list.filter { note -> !note.lock }
            noteAdapter.differ.submitList(unlockedNotes)
        }
    }
}
