package firebase.template.Database

import androidx.room.Database
import androidx.room.RoomDatabase

class NotesDatabase {
    @Database(entities = [NoteData::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun notesDao(): NotesDao
    }
}
