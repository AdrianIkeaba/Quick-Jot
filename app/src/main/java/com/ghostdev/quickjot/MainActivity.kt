package com.ghostdev.quickjot

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.ghostdev.quickjot.database.NoteDatabase
import com.ghostdev.quickjot.databinding.ActivityMainBinding
import com.ghostdev.quickjot.repository.NotesRepository
import com.ghostdev.quickjot.viewmodel.NoteViewModel
import com.ghostdev.quickjot.viewmodel.NoteViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerToggle: ActionBarDrawerToggle

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
        val navController = navHostFragment.navController
        drawerLayout = binding.drawerLayout

        // Set up the ActionBarDrawerToggle
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set up the AppBarConfiguration with top-level destinations to show the home/up button
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up the toolbar with the NavController and AppBarConfiguration
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)

        // Add the OnDestinationChangedListener
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    supportActionBar?.title = "All Jots"
                    val firstMenuItem: MenuItem = binding.navigationView.menu.getItem(0)
                    firstMenuItem.isChecked = true
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
        val roundedDrawable = ContextCompat.getDrawable(this, R.drawable.nav_drawer_background)
        ViewCompat.setBackground(binding.navigationView, roundedDrawable)

       binding.navigationView.inflateHeaderView(R.layout.nav_drawer_header)

        val navView: NavigationView = binding.navigationView
        val menu: Menu = navView.menu
        for (i in 0 until menu.size()) {
                val menuItem: MenuItem = menu.getItem(i)
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(RelativeSizeSpan(1.25f), 0, spanString.length, 0)
                menuItem.title = spanString
        }
    }

    private fun setUpViewModel() {
        val notesRepository = NotesRepository(NoteDatabase(this))
        val viewModelProviderFactory = NoteViewModelFactory(application, notesRepository)
        noteViewModel = ViewModelProvider(this, viewModelProviderFactory).get(NoteViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
