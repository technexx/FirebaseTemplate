package firebase.template.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes_data")
    fun getAllNotesData(): List<NoteData>

    @Query("SELECT * FROM notes_data WHERE uid IN (:userIds)")
    fun getAllNotesById(userIds:IntArray):List<NoteData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(vararg notes_data:NoteData)

    @Update
    fun updateNotes(note: NoteData)

    @Delete
    fun deleteNotes(note: NoteData)
}