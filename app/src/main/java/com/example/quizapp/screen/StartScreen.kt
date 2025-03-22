package com.example.quizapp.screen


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.quizapp.route.Routes
import com.example.quizapp.viewmodel.QuizViewModel

@Composable
fun StartScreen(viewModel: QuizViewModel, navController: NavController) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    ) {
        // Background Layer
        DrawPointsScreen()

        // Foreground Layer (Column Content)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Add padding to avoid overlap with the keyboard
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center, // Align content to the top to allow collapsing
            horizontalAlignment = CenterHorizontally
        ) {
            WelcomeCard(viewModel, navController)
        }
    }
}



@Composable
fun WelcomeCard(viewModel: QuizViewModel, navController: NavController) {
    // State to track the input text
    var name by remember { mutableStateOf("") }
    val isError = remember { mutableStateOf(false) } // Use MutableState for isError

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // Add spacing for better layout
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { newText ->
                    name = newText
                    isError.value = newText.isBlank() // Update MutableState
                },
                label = { Text("Name") },
                singleLine = true,
                isError = isError.value, // Use MutableState value
                modifier = Modifier
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = if (isError.value) Color.Red else Color.LightGray,
                    focusedBorderColor = if (isError.value) Color.Red else Color.Green
                )
            )

            // Button
            StartButton(viewModel, name, isError, navController) // Pass MutableState

            if (isError.value) {
                Text(
                    text = "Please enter your name",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

}


@Composable
fun StartButton(
    viewModel: QuizViewModel,
    name: String,
    isError: MutableState<Boolean>,
    navController: NavController
) {

    Button(
        onClick = {
            if (name.isBlank()) {
                isError.value = true // Update MutableState
            } else {
                viewModel.getData()
                navController.navigate(Routes.quizScreen + "/$name")
            }
        },
        enabled = name.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EA)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = ButtonDefaults.buttonElevation(2.dp)
    ) {
        Text(
            text = "START",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )
    }

}


@Composable
fun DrawPointsScreen() {

    Column(
        modifier = Modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xff507ee7),
                    Color(0xff881ffa)
                )
            )
        )
    ) {
        Box {
            CurvedLineCanvas()
        }
    }

}


@Composable
fun CurvedLineCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {

        val canvasHeight = size.height
        val canvasWidth = size.width

        val bottomPath = Path().apply {
            moveTo(x = 0f, y = canvasHeight)
            quadraticTo(
                canvasWidth / 1.5f, canvasHeight / 1.40f, // control point
                canvasWidth, canvasHeight / 1.15f       // End point
            )
            lineTo(canvasWidth, canvasHeight) // Line to bottom-right corner
            lineTo(0f, canvasHeight)          // Line back to bottom-left corner
            close()
        }

        drawPath(
            path = bottomPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF8721F4), Color(0xFF8E15FC)
                )
            ),
            alpha = .90f
        )

        val topStartPath = Path().apply {
            moveTo(x = 0f, y = canvasHeight / 8)
            quadraticTo(
                canvasWidth / 5.5f, canvasHeight / 5.65f, // control point
                canvasWidth / 2.55f, canvasHeight / 9 // End point
            )
            lineTo(x = canvasWidth / 3, y = 0f)           // Line to top-Right
            lineTo(x = 0f, y = 0f)                    // Line back to top-left corner
            close()
        }

        drawPath(
            path = topStartPath,
            color = Color(0xFF5371E9),
            alpha = 90f
        )

        val topRightPath = Path().apply {
            moveTo(x = canvasWidth / 4, y = 0f)
            quadraticTo(
                canvasWidth / 1.75f, canvasHeight / 2.85f, // control point
                canvasWidth, canvasHeight / 5 // End point
            )
            lineTo(x = canvasWidth, y = 0f)           // Line to top-Right
            lineTo(x = 0f, y = 0f)                    // Line back to top-left corner
            close()
        }

        drawPath(
            path = topRightPath,
            color = Color(0xFF5B76E4),
            alpha = 90f
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun StartScreenPreview() {
    val viewModel = QuizViewModel()
    val navController = rememberNavController()
    StartScreen(viewModel, navController)
}