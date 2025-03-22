package com.example.quizapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASEURL = "https://opentdb.com"


    private fun getInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val quizApi = getInstance().create(QuizApiService::class.java)

}