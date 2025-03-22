package com.example.quizapp.screen


import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quizapp.api.QuizQuestionApi
import com.example.quizapp.route.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuizScreen(data: QuizQuestionApi,navController: NavController,name: String) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        QuestionOptions(data,navController,name)
    }
}


@OptIn(DelicateCoroutinesApi::class)
@Composable
fun QuestionOptions(data: QuizQuestionApi, navController: NavController, name: String) {
    // State to track the current question index
    val currentIndex = remember { mutableIntStateOf(0) }
    val selectedOption = remember { mutableStateOf<String?>(null) } // Tracks the selected option
    val validated = remember { mutableStateOf(false) } // Tracks if the answer has been validated
    val shuffledOptions = remember { mutableStateListOf<String>() } // Holds shuffled options
    val correctAnswerCount = remember { mutableIntStateOf(0) } // Tracks the number of correct answers
    val isFinished = remember { mutableStateOf(false) } // Tracks if the quiz is finished

    // Shuffle options when the question changes
    LaunchedEffect(currentIndex.intValue) {
        val currentQuestion = data.results[currentIndex.intValue]
        shuffledOptions.clear()
        shuffledOptions.addAll((listOf(currentQuestion.correct_answer) + currentQuestion.incorrect_answers).shuffled())
        selectedOption.value = null // Reset selected option for the new question
        validated.value = false // Reset validation for the new question
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Progress Bar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Question ${currentIndex.intValue + 1} of ${data.results.size}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LinearProgressIndicator(
                progress = (currentIndex.intValue + 1).toFloat() / data.results.size.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF6200EA) // Purple for the progress indicator
            )
        }

        // Check if results are not empty and currentIndex is within bounds
        if (data.results.isNotEmpty() && currentIndex.intValue in data.results.indices) {
            // Display the current question
            Text(
                modifier = Modifier.padding(vertical = 32.dp),
                text = data.results[currentIndex.intValue].question,
                fontSize = 24.sp
            )

            // Display shuffled options
            shuffledOptions.forEach { option ->
                QuestionOptionText(
                    text = option,
                    isSelected = selectedOption.value == option,
                    isCorrect = option == data.results[currentIndex.intValue].correct_answer,
                    isShowCorrectAnswer = validated.value,
                    onClick = {
                        selectedOption.value = option
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Text(text = "Result list is empty")
        }

        // NEXT or FINISH Button
        Button(
            onClick = {
                validated.value = true // Trigger validation

                // Count correct answer
                if (selectedOption.value == data.results[currentIndex.intValue].correct_answer) {
                    correctAnswerCount.intValue += 1
                }

                if (currentIndex.intValue < data.results.size - 1) {
                    // Move to the next question after 1 second
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(1000)
                        currentIndex.intValue += 1 // Move to the next question
                        selectedOption.value = null // Reset selected option
                        validated.value = false // Reset validation
                    }
                } else {
                    isFinished.value = true // Mark quiz as finished
                    CoroutineScope(Dispatchers.IO).launch {
                        kotlinx.coroutines.delay(1000)
                        withContext(Dispatchers.Main) {
                            navController.navigate("${Routes.resultScreen}/${correctAnswerCount.intValue}/${data.results.size}/${name}")
                        }
                    }
                }
            },
            enabled = selectedOption.value != null, // Enable only when an option is selected
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedOption.value != null) Color(0xFF6200EA) else Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = if (currentIndex.intValue < data.results.size - 1) "NEXT" else "FINISH",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}


@Composable
fun QuestionOptionText(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isShowCorrectAnswer: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12.dp),
                color = when {
                    isCorrect && isShowCorrectAnswer -> Color.Green // Correct answer border
                    isSelected && isShowCorrectAnswer -> Color.Red // Incorrect selected answer border
                    isSelected -> Color(0xFF6200EA) // Purple for selected option
                    else -> Color.LightGray // Default border color
                }
            )
            .clickable(enabled = !isShowCorrectAnswer) { onClick() }, // Disable click after validation
        shape = RoundedCornerShape(12.dp),
        color = when {
            isCorrect && isShowCorrectAnswer -> Color(0xFF00FF00) // Green surface for correct answer
            isSelected && isShowCorrectAnswer -> Color(0xFFFF4444) // Red surface for incorrect selected answer
            else -> Color.White // Default surface
        },
        shadowElevation = 2.dp
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(16.dp),
            color = when {
                isCorrect && isShowCorrectAnswer -> Color.Black // White text for correct answer
                isSelected && isShowCorrectAnswer -> Color.Black // Black text for incorrect selected answer
                else -> Color.Black // Default text color
            }
        )
    }
}

