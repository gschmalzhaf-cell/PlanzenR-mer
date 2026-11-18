package com.roemerplant.app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Zentrale, moderne Datums-Formatierung (java.time statt SimpleDateFormat).
 */
object DateFormats {

    private val zone: ZoneId get() = ZoneId.systemDefault()

    private val shortDate: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd. MMM", Locale.GERMANY)
    private val longDate: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd. MMM yyyy", Locale.GERMANY)
    private val dateTime: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY)

    fun formatShort(epochMillis: Long): String =
        shortDate.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun formatLong(epochMillis: Long): String =
        longDate.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun formatDateTime(epochMillis: Long): String =
        dateTime.format(Instant.ofEpochMilli(epochMillis).atZone(zone))

    fun formatNowDateTime(): String =
        dateTime.format(java.time.ZonedDateTime.now(zone))
}
