package firebase.template.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notes_data", indices = [Index(value = ["title"], unique = false)])
data class NoteData(
    @PrimaryKey(autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "body") val body: String,
    @ColumnInfo(name = "lastEdited") val lastEdited: String,
)