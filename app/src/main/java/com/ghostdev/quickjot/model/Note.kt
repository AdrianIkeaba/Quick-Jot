package com.ghostdev.quickjot.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("noteTitle")
    val noteTitle: String,
    @ColumnInfo("noteBody")
    val noteBody: String,
    @ColumnInfo("lock")
    val lock: Boolean
): Parcelable
