package hr.ferit.matijasokol.todoapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import hr.ferit.matijasokol.todoapp.other.Constants.TaskKeys.TASK_TABLE
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = TASK_TABLE)
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {

    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}