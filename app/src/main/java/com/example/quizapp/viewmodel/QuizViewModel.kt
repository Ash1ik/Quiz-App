package com.example.quizapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.api.NetworkResponse
import com.example.quizapp.api.QuizQuestionApi
import com.example.quizapp.api.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private val quizQuestionApi = RetrofitInstance.quizApi
    private val _quizQuestionApiResult = MutableLiveData<NetworkResponse<QuizQuestionApi>>()
    val quizQuestionApiResult : LiveData<NetworkResponse<QuizQuestionApi>> = _quizQuestionApiResult

    fun getData(){
        _quizQuestionApiResult.value = NetworkResponse.Loading

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val response = quizQuestionApi.getQuizQuestions()

                response.isSuccessful
                if (response.isSuccessful){
                    response.body()?.let {
                        _quizQuestionApiResult.value = NetworkResponse.Success(it)

                    }
                }else{
                    _quizQuestionApiResult.value = NetworkResponse.Error("Failed to get data")
                }
            }catch (e: Exception) {
                _quizQuestionApiResult.value = NetworkResponse.Error("Failed to get data")
            }
        }
    }

}
