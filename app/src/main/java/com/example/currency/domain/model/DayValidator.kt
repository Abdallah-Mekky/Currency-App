package com.example.currency.domain.model

data class DayValidator(
    val prev:Boolean = false,
    val currentDay: String = "",
    val after:Boolean = true
)
