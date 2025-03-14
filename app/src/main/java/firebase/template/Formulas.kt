package firebase.template

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

val dateStringFormat = "MMM dd, yyyy"
val timeStringFormat = "hh:mm a"
val dateAndTimeStringFormat = "MMM dd, yyyy - hh:mm a"

fun formattedDateAndTime(): String{
    return formattedDate() + " - " + formattedTime()
}

fun formattedTime(): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date())
}

fun formattedDate(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now().format(DateTimeFormatter.ofPattern(dateStringFormat))
    } else {
        SimpleDateFormat(dateStringFormat, Locale.getDefault()).format(Date())
    }
}

fun undoChanges(originalString: String, modifiedString: String): String {
    if (originalString.length == modifiedString.length) {
        return originalString // No changes to undo
    }

    if (originalString.length > modifiedString.length) {
        // Add a character back to the modified string
        if (modifiedString.length < originalString.length) {
            return modifiedString + originalString[modifiedString.length]
        } else {
            return modifiedString
        }

    } else {
        // Handle the case where modifiedString is longer than originalString
        // Remove the last word added
        val wordsModified = modifiedString.split(" ")
        val wordsOriginal = originalString.split(" ")
        if (wordsModified.size > wordsOriginal.size) {
            return wordsModified.subList(0, wordsModified.size - 1).joinToString(" ")
        } else {
            return modifiedString
        }
    }
}