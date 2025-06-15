package com.example.currency.domain.model

/**
 * Represent current day with own validator
 */
data class DayValidator(
    val prev:Boolean = false,
    val currentDay: String = "",
    val after:Boolean = true
)
