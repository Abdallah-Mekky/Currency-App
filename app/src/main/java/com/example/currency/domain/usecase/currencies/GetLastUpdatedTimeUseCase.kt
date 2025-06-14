package com.example.currency.domain.usecase.currencies

import com.example.currency.data.source.local.preferences.SharedPrefs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetLastUpdatedTimeUseCase @Inject constructor(private val sharedPrefs: SharedPrefs) {
    suspend operator fun invoke() = sharedPrefs.getLastUpdate()
}