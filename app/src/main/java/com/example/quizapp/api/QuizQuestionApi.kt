package com.example.quizapp.api

data class QuizQuestionApi(
    val response_code: Int,
    val results: List<Result>
)