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
    val originalWords = originalString.split(" ")
    val modifiedWords = modifiedString.split(" ")

    if (modifiedWords.size > originalWords.size) {
        // Remove the last added word
        return modifiedWords.subList(0, modifiedWords.size - 1).joinToString(" ")
    } else if (modifiedWords.size < originalWords.size) {
        // Add a letter that was removed.
        if (modifiedString.length < originalString.length) {
            return modifiedString + originalString[modifiedString.length]
        } else {
            return modifiedString
        }
    } else {
        // Check for letter additions/subtractions within existing words
        val minWords = minOf(originalWords.size, modifiedWords.size)
        val resultWords = mutableListOf<String>()

        for (i in 0 until minWords) {
            if (originalWords[i].length < modifiedWords[i].length) {
                // Remove extra letters from the current word
                resultWords.add(modifiedWords[i].substring(0, originalWords[i].length))
            } else if (originalWords[i].length > modifiedWords[i].length) {
                //Add a letter back to the current word
                if (modifiedWords[i].length < originalWords[i].length){
                    resultWords.add(modifiedWords[i] + originalWords[i][modifiedWords[i].length])
                } else {
                    resultWords.add(modifiedWords[i])
                }
            } else {
                resultWords.add(modifiedWords[i])
            }
        }

        // Add remaining original words if any
        if(originalWords.size > modifiedWords.size){
            resultWords.addAll(originalWords.subList(minWords, originalWords.size))
        }

        return resultWords.joinToString(" ")
    }
}