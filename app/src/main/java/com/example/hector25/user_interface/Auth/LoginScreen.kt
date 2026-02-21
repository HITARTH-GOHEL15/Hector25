package com.example.hector25.user_interface.Auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hector25.navigation.Screens

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var loginPressed by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Observe ViewModel state
    val loginState by loginViewModel.state.collectAsState()

    // Derived loading/error/success flags
    val isLoading = loginState is LoginState.Loading

    // React to state changes
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                navController.navigate(Screens.HomeScreenRoute) {
                    popUpTo(Screens.LoginScreenRoute) { inclusive = true }
                }
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // App Logo/Icon
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color(0xFF2563EB),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "App Logo",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                color = Color(0xFF6B7280)
            )

            // Error banner
            if (loginState is LoginState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDED)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Invalid email or password. Please try again.",
                            fontSize = 13.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email or Username") },
                placeholder = { Text("Enter your email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Color(0xFF6B7280)
                    )
                },
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        IconButton(onClick = { email = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = Color(0xFF6B7280)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Check else Icons.Default.Clear,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Remember Me & Forgot Password Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2563EB),
                            uncheckedColor = Color(0xFF9CA3AF)
                        )
                    )
                    Text(
                        text = "Remember me",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
                Text(
                    text = "Forgot Password?",
                    fontSize = 14.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* Handle forgot password */ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button — triggers ViewModel
            Button(
                onClick = {
                    loginPressed = true
                    loginViewModel.logIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(if (loginPressed) 0.97f else 1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider with "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                Text(text = "OR", fontSize = 14.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Medium)
                Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
            }

            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginButton(
                text = "Continue with Google",
                icon = Icons.Default.AccountCircle,
                backgroundColor = Color.White,
                textColor = Color(0xFF1F2937),
                onClick = { /* Handle Google login */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialLoginButton(
                text = "Continue with Apple",
                icon = Icons.Default.AddCircle,
                backgroundColor = Color(0xFF1F2937),
                textColor = Color.White,
                onClick = { /* Handle Apple login */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            val signUpText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 14.sp)) {
                    append("Don't have an account? ")
                }
                withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
                    append("Sign Up")
                }
            }
            Text(
                text = signUpText,
                modifier = Modifier.clickable { navController.navigate(Screens.SignUpScreenRoute) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SocialLoginButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    OutlinedButton(
        onClick = { isPressed = true; onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(if (isPressed) 0.97f else 1f),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 1.dp,
            brush = SolidColor(Color(0xFFE5E7EB))
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme { }
}