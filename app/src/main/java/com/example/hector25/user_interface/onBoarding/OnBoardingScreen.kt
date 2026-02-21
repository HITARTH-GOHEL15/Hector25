package com.example.hector25.user_interface.onBoarding


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hector25.navigation.Screens

@Composable
fun WelcomeOnboardingScreen(
    navController: NavController
) {
    var signUpPressed by remember { mutableStateOf(false) }
    var loginPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo/Icon
            HouseHandIcon()

            Spacer(modifier = Modifier.height(60.dp))

            // Title
            Text(
                text = "Find your dream home",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Explore properties for sale or rent in your area with a modern and intuitive interface.",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Sign Up Button
            DynamicWelcomeButton(
                text = "Sign Up",
                isPrimary = true,
                isPressed = signUpPressed,
                onClick = {
                    signUpPressed = !signUpPressed
                    // Handle sign up navigation
                    navController.navigate(Screens.SignUpScreenRoute)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            DynamicWelcomeButton(
                text = "Login",
                isPrimary = false,
                isPressed = loginPressed,
                onClick = {
                    loginPressed = !loginPressed
                    // Handle login navigation
                    navController.navigate(Screens.LoginScreenRoute)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun HouseHandIcon() {
    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val iconSize = size.width
            val strokeWidth = 12.dp.toPx()
            val color = Color(0xFF2563EB)

            // Draw house outline
            val housePath = Path().apply {
                // Roof
                moveTo(iconSize * 0.5f, iconSize * 0.25f)
                lineTo(iconSize * 0.75f, iconSize * 0.4f)
                lineTo(iconSize * 0.75f, iconSize * 0.35f)
                lineTo(iconSize * 0.8f, iconSize * 0.35f)
                lineTo(iconSize * 0.8f, iconSize * 0.45f)

                // Right wall
                lineTo(iconSize * 0.8f, iconSize * 0.55f)

                // Window on house
                moveTo(iconSize * 0.62f, iconSize * 0.38f)
                lineTo(iconSize * 0.67f, iconSize * 0.38f)
                moveTo(iconSize * 0.62f, iconSize * 0.42f)
                lineTo(iconSize * 0.67f, iconSize * 0.42f)
                moveTo(iconSize * 0.645f, iconSize * 0.38f)
                lineTo(iconSize * 0.645f, iconSize * 0.42f)
            }

            drawPath(
                path = housePath,
                color = color,
                style = Stroke(width = strokeWidth)
            )

            // Draw hand outline
            val handPath = Path().apply {
                // Wrist/sleeve
                moveTo(iconSize * 0.25f, iconSize * 0.75f)
                lineTo(iconSize * 0.25f, iconSize * 0.6f)
                lineTo(iconSize * 0.3f, iconSize * 0.6f)
                lineTo(iconSize * 0.3f, iconSize * 0.5f)

                // Palm bottom
                moveTo(iconSize * 0.3f, iconSize * 0.75f)
                lineTo(iconSize * 0.7f, iconSize * 0.75f)

                // Fingers curve
                lineTo(iconSize * 0.75f, iconSize * 0.7f)
                lineTo(iconSize * 0.75f, iconSize * 0.55f)

                // Palm top connecting to fingers
                moveTo(iconSize * 0.3f, iconSize * 0.5f)
                lineTo(iconSize * 0.7f, iconSize * 0.5f)
                lineTo(iconSize * 0.75f, iconSize * 0.55f)
            }

            drawPath(
                path = handPath,
                color = color,
                style = Stroke(width = strokeWidth)
            )

            // Draw building/pillar on left
            val buildingPath = Path().apply {
                moveTo(iconSize * 0.25f, iconSize * 0.75f)
                lineTo(iconSize * 0.25f, iconSize * 0.45f)
                lineTo(iconSize * 0.35f, iconSize * 0.45f)
                lineTo(iconSize * 0.35f, iconSize * 0.75f)
            }

            drawPath(
                path = buildingPath,
                color = color,
                style = Stroke(width = strokeWidth)
            )
        }
    }
}

@Composable
fun DynamicWelcomeButton(
    text: String,
    isPrimary: Boolean,
    isPressed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = if (isPressed) 0.97f else 1f

    Button(
        onClick = onClick,
        modifier = modifier
            .height(58.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPrimary) Color(0xFF2563EB) else Color(0xFFDCEEFF)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isPrimary) 2.dp else 0.dp,
            pressedElevation = if (isPrimary) 6.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isPrimary) Color.White else Color(0xFF2563EB)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeOnboardingScreenPreview() {
    MaterialTheme {

    }
}