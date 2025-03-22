package com.example.quizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.api.NetworkResponse
import com.example.quizapp.route.Routes
import com.example.quizapp.screen.QuizScreen
import com.example.quizapp.screen.ResultScreen
import com.example.quizapp.screen.StartScreen
import com.example.quizapp.viewmodel.QuizViewModel
import kotlin.toString


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window,false)
        super.onCreate(savedInstanceState)
        setContent {

            val viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
            val navController = rememberNavController()
            val quizResult = viewModel.quizQuestionApiResult.observeAsState()


            NavHost(navController = navController, startDestination = Routes.startScreen){
                composable(Routes.startScreen){
                    StartScreen(viewModel,navController)
                }
                composable(Routes.quizScreen + "/{name}") {
                    val name = it.arguments?.getString("name").toString()

                    when(val result = quizResult.value){
                        is NetworkResponse.Success -> {
                            QuizScreen(result.data,navController,name)
                        }
                        is NetworkResponse.Error -> {
                            Text(text  = result.exception)
                        }
                        NetworkResponse.Loading -> {
                            CircularProgressIndicator()
                        }
                        null ->{}
                    }
                }
                composable("${Routes.resultScreen}/{correctAnswersCount}/{totalQuestions}/{name}") { backStackEntry ->
                    val correctAnswersCount = backStackEntry.arguments?.getString("correctAnswersCount")?.toIntOrNull() ?: 0
                    val totalQuestions = backStackEntry.arguments?.getString("totalQuestions")?.toIntOrNull() ?: 0
                    val userName = backStackEntry.arguments?.getString("name") ?: "Ashik Iqbal"
                    ResultScreen(correctAnswersCount,totalQuestions,userName)
                }
            }
        }
    }
}

