package com.example.quizapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.quizapp.R

@Composable
fun ResultScreen(correctAnswersCount: Int,totalQuestions: Int,userName : String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$correctAnswersCount/$totalQuestions",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            ),
            color = if (correctAnswersCount < totalQuestions * 0.4f) Color.Red
                    else Color.Green
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (correctAnswersCount > totalQuestions * 0.4f)"Quiz Finished! Congrats\n$userName"
                else "Better Luck Next Time\n$userName",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp
            ),
            textAlign = TextAlign.Center
        )


        if (correctAnswersCount > totalQuestions * 0.4f){
            LottieAnimationSuccess()
        }else{
            LottieAnimationFailed()
        }

    }
}


@Composable
fun LottieAnimationSuccess() {
    // Load the Lottie composition from a JSON file in the raw resources folder
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success))

    // State to track animation visibility
    var isAnimationVisible by remember { mutableStateOf(true) }

    // Lottie animation progress state
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 100 // Play animation only once
    )

    // Check if the animation has finished
    LaunchedEffect(progress) {
        if (progress == 1f) { // Animation has reached the end
            isAnimationVisible = false
        }
    }

    // Show the animation if it's still visible
    if (isAnimationVisible) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress }, // Attach the progress to the animation
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Customize size as needed
            )
        }
    }
}

@Composable
fun LottieAnimationFailed() {
    // Load the Lottie composition from a JSON file in the raw resources folder
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.failed))

    // State to track animation visibility
    var isAnimationVisible by remember { mutableStateOf(true) }

    // Lottie animation progress state
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 100 // Play animation only once
    )

    // Check if the animation has finished
    LaunchedEffect(progress) {
        if (progress == 1f) { // Animation has reached the end
            isAnimationVisible = false
        }
    }

    // Show the animation if it's still visible
    if (isAnimationVisible) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress }, // Attach the progress to the animation
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Customize size as needed
            )
        }
    }
}
