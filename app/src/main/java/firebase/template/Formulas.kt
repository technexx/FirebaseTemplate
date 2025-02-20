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