package com.ghostdev.quickjot

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.ghostdev.quickjot.database.NoteDatabase
import com.ghostdev.quickjot.databinding.ActivityMainBinding
import com.ghostdev.quickjot.repository.NotesRepository
import com.ghostdev.quickjot.viewmodel.NoteViewModel
import com.ghostdev.quickjot.viewmodel.NoteViewModelFactory

class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpViewModel()
        supportActionBar?.title = "All Jots"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        var navController = navHostFragment.navController


        // Set up the AppBarConfiguration with top-level destinations to show the home/up button
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment))
        // Setup the toolbar with the NavController and AppBarConfiguration
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        // Add the OnDestinationChangedListener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    supportActionBar?.title = "All Jots"
                }
                R.id.updateNoteFragment -> {
                    supportActionBar?.title = "Update Jot"
                }
                R.id.newNoteFragment -> {
                    supportActionBar?.title = "New Jot"
                }
                else -> {
                    supportActionBar?.title = "All Jots"
                }
            }
        }

    }

    private fun setUpViewModel() {
        val notesRepository = NotesRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, notesRepository)
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory) [NoteViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }
}