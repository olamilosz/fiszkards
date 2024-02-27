package com.example.fiszki.data.database.entity

data class Round(
    val roundNumber: Int = 0,
    var correctAnswerCount: Int? = 0,
    var roundListSize: Int? = 0
)