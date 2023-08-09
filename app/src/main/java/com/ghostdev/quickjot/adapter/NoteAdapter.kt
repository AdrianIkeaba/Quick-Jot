package com.ghostdev.quickjot.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghostdev.quickjot.R
import com.ghostdev.quickjot.databinding.NoteLayoutBinding
import com.ghostdev.quickjot.fragments.UpdateNoteFragment
import com.ghostdev.quickjot.model.Note
import java.util.Random

class NoteAdapter(): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(val itemBinding: NoteLayoutBinding): RecyclerView.ViewHolder(itemBinding.root)
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NoteLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        if (!currentNote.lock) {
            holder.itemBinding.tvNoteTitle.text = currentNote.noteTitle
            holder.itemBinding.tvNoteBody.text = currentNote.noteBody

            val random = Random()
            val color = Color.argb(255,
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256))

            holder.itemBinding.ibColor.setBackgroundColor(color)
        }

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("note", currentNote!!)
            val destinationFragment = UpdateNoteFragment()
            destinationFragment.arguments = bundle

            it.findNavController().navigate(R.id.action_homeFragment_to_updateNoteFragment, bundle)

        }
    }
    fun setNotes(notes: List<Note>) {
        val notLockedNotes = notes.filter { !it.lock }
        differ.submitList(notLockedNotes)
    }
}