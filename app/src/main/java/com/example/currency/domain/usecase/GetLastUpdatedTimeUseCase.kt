package com.example.currency.domain.usecase

import com.example.currency.data.source.local.preferences.SharedPrefs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetLastUpdatedTimeUseCase @Inject constructor(private val sharedPrefs: SharedPrefs) {
    suspend operator fun invoke() = formatTimestamp(sharedPrefs.getLastUpdate())

    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd-MM-yyyy - HH:mm", Locale.ENGLISH)
        return format.format(date)
    }
}