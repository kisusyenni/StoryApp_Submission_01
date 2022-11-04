package com.kisusyenni.storyapp.helper
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun formatDate(dateString: String): String {
    val pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)
    val format = SimpleDateFormat(pattern, locale)
    val date = format.parse(dateString) as Date
    return DateFormat.getDateInstance(DateFormat.FULL).format(date)

//    return SimpleDateFormat(
//        pattern,
//        Locale.US
//    ).format(System.currentTimeMillis())

//    val instant = Instant.parse(dateString)
//    val formatter = DateTimeFormatter.ofPattern(pattern)
//        .withZone(ZoneId.of(timezone))
//    return formatter.format(instant)

//    LocalDateTime.parse(datetime).toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.US))
}