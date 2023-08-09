package com.ghostdev.quickjot.adapter

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghostdev.quickjot.FingerprintHelper
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.databinding.LockedNoteBinding
import com.ghostdev.quickjot.fragments.UpdateNoteFragment
import com.ghostdev.quickjot.model.Note
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Random

class LockedNoteAdapter : RecyclerView.Adapter<LockedNoteAdapter.LockedNoteViewHolder>() {

    class LockedNoteViewHolder(val itemBinding: LockedNoteBinding): RecyclerView.ViewHolder(itemBinding.root)
    private val differCallback = object: DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id  &&
                    oldItem.noteBody == newItem.noteBody &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LockedNoteViewHolder {
        return LockedNoteViewHolder(LockedNoteBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: LockedNoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        if (currentNote.lock) {
            holder.itemBinding.tvNoteTitle.text = currentNote.noteTitle

            val random = Random()
            val color = Color.argb(
                255,
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            )

            holder.itemBinding.ibColor.setBackgroundColor(color)
        }
        holder.itemView.setOnClickListener {
            val sharedPreferences = it.context.getSharedPreferences("jot", Context.MODE_PRIVATE)
            val hasPass = sharedPreferences.getBoolean("hasPass", false)
            val passType = sharedPreferences.getInt("passType", 0)

            if (hasPass) {
                if (passType == 1) {
                    // Show a dialog with an EditText for password input
                    val dialogBuilder = AlertDialog.Builder(it.context, R.style.MyDialogTheme)
                    val inflater = LayoutInflater.from(it.context)
                    val dialogView = inflater.inflate(R.layout.dialog_password, null)
                    val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
                    val cancelButton = dialogView.findViewById<TextView>(R.id.cancel_button)
                    val doneButton = dialogView.findViewById<TextView>(R.id.done_button)

                    val alertDialog = dialogBuilder.setView(dialogView).create()

                    editTextPassword.setOnFocusChangeListener { view, hasFocus ->
                        if (hasFocus) {
                            val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                        }
                    }

                    doneButton.setOnClickListener { _ ->
                        if (editTextPassword.text.isNullOrEmpty()) {
                            Toast.makeText(dialogView.context, "Enter a password", Toast.LENGTH_SHORT).show()
                        } else {
                            val savedPassword = sharedPreferences.getString("password", "")
                            val password = editTextPassword.text.toString()
                            if (password == savedPassword) {
                                val bundle = Bundle()
                                bundle.putParcelable("note", currentNote!!)
                                val destinationFragment = UpdateNoteFragment()
                                destinationFragment.arguments = bundle
                                it.findNavController().navigate(R.id.action_lockedNoteFragment_to_updateNoteFragment, bundle)
                            } else {
                                Toast.makeText(it.context, "Wrong Password", Toast.LENGTH_SHORT).show()
                            }
                        }
                        alertDialog.dismiss()
                    }

                    cancelButton.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    alertDialog.show() // Show the dialog

                } else if (passType == 2) {
                    val fingerprintHelper = FingerprintHelper(it.context, ContextCompat.getMainExecutor(it.context))

                    fingerprintHelper.showFingerprintPrompt(
                        onSuccess = {
                            val bundle = Bundle()
                            bundle.putParcelable("note", currentNote!!)
                            val destinationFragment = UpdateNoteFragment()
                            destinationFragment.arguments = bundle

                            it.findNavController().navigate(
                                R.id.action_lockedNoteFragment_to_updateNoteFragment,
                                bundle
                            )
                        },
                        onError = {

                        }
                    )
                }
            } else {
                var options = arrayOf("Password")
                val fingerprintHelper =
                    FingerprintHelper(it.context, ContextCompat.getMainExecutor(it.context))
                val hasFingerprint = fingerprintHelper.isFingerprintAvailable()

                if (hasFingerprint) {
                    options += "Fingerprint"
                }

                MaterialAlertDialogBuilder(it.context)
                    .setTitle("Select Unlock Method")
                    .setItems(options) { _, which ->
                        when (which) {
                            0 -> {
                                // Show a dialog with an EditText for password input
                                val dialogBuilder = AlertDialog.Builder(it.context)
                                val inflater = LayoutInflater.from(it.context)
                                val dialogView = inflater.inflate(R.layout.dialog_password, null)
                                val editTextPassword = dialogView.findViewById<EditText>(R.id.editTextPassword)
                                val cancelButton = dialogView.findViewById<TextView>(R.id.cancel_button)
                                val doneButton = dialogView.findViewById<TextView>(R.id.done_button)

                                val dialog = dialogBuilder.setView(dialogView).create()

                                doneButton.setOnClickListener { _ ->
                                    if (editTextPassword.text.isNullOrEmpty()) {
                                        Toast.makeText(it.context, "Enter  password", Toast.LENGTH_SHORT).show()
                                    } else {
                                        sharedPreferences.edit {
                                            putString("password", editTextPassword.text.toString())
                                            putBoolean("hasPass", true)
                                            putInt("passType", 1)
                                        }
                                    }
                                    dialog.dismiss()
                                }

                                cancelButton.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()

                            }

                            1 -> {
                                sharedPreferences.edit {
                                    putBoolean("hasPass", true)
                                    putInt("passType", 2)
                                }

                                fingerprintHelper.showFingerprintPrompt(
                                    onSuccess = {
                                        val bundle = Bundle()
                                        bundle.putParcelable("note", currentNote!!)
                                        val destinationFragment = UpdateNoteFragment()
                                        destinationFragment.arguments = bundle

                                        it.findNavController().navigate(
                                            R.id.action_lockedNoteFragment_to_updateNoteFragment,
                                            bundle
                                        )
                                    },
                                    onError = {

                                    }
                                )
                            }
                        }
                    }
                    .show()
            }
        }
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setNotes(notes: List<Note>) {
        val lockedNotes = notes.filter { it.lock }
        differ.submitList(lockedNotes)
    }

}
