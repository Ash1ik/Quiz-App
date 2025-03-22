package com.example.quizapp.api


import retrofit2.Response
import retrofit2.http.GET

interface QuizApiService {

    @GET("api.php?amount=10&category=18&difficulty=easy&type=multiple")
    suspend fun getQuizQuestions() : Response<QuizQuestionApi>

}