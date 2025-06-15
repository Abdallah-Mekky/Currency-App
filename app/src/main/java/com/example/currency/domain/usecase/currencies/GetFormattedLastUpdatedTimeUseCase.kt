package com.example.currency.domain.usecase.currencies

import com.example.currency.data.source.local.preferences.DataStores
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case class responsible for retrieving and formatting the last updated time
 * of currency rates stored in shared preferences.
 *
 * It converts a stored timestamp into a human-readable date and time string.
 *
 * @property dataStores A helper class for accessing shared preferences where the last update timestamp is stored.
 */
class GetFormattedLastUpdatedTimeUseCase @Inject constructor(private val dataStores: DataStores) {
    suspend operator fun invoke() = formatTimestamp(dataStores.lastUpdateTime.first())

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd-MM-yyyy - HH:mm", Locale.ENGLISH)
        return format.format(date)
    }
}