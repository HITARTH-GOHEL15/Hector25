package com.example.hector25.user_interface.Auth

import android.util.Patterns
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hector25.navigation.Screens

@Composable
fun SignUpScreen(
    navController: NavController,
    logUpViewModel: LogUpViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }
    var signUpPressed by remember { mutableStateOf(false) }
    var userType by remember { mutableStateOf("Buyer") }

    val focusManager = LocalFocusManager.current

    // Observe ViewModel state
    val logUpState by logUpViewModel.state.collectAsState()

    val isLoading = logUpState is LogUpState.Loading

    val passwordStrength = remember(password) {
        when {
            password.length < 8 -> "Weak"
            password.length < 12 && password.any { it.isDigit() } -> "Medium"
            password.length >= 12 && password.any { it.isDigit() } && password.any { it.isUpperCase() } -> "Strong"
            else -> "Weak"
        }
    }
    val passwordMatch = password == confirmPassword && confirmPassword.isNotEmpty()

    // React to state changes
    LaunchedEffect(logUpState) {
        when (logUpState) {
            is LogUpState.Success -> {
                navController.navigate(Screens.HomeScreenRoute) {
                    popUpTo(Screens.SignUpScreenRoute) { inclusive = true }
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
            Spacer(modifier = Modifier.height(40.dp))

            // App Logo/Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = Color(0xFF2563EB), shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "App Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Create Account", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Sign up to get started", fontSize = 16.sp, color = Color(0xFF6B7280))

            // Error banner
            if (logUpState is LogUpState.Error) {
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
                            text = "Sign up failed. Please check your details and try again.",
                            fontSize = 13.sp,
                            color = Color(0xFFEF4444)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // User Type Selection
            Text(
                text = "I am a:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4B5563),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                UserTypeChip(text = "Buyer", isSelected = userType == "Buyer", onClick = { userType = "Buyer" }, modifier = Modifier.weight(1f))
                UserTypeChip(text = "Seller", isSelected = userType == "Seller", onClick = { userType = "Seller" }, modifier = Modifier.weight(1f))
                UserTypeChip(text = "Agent", isSelected = userType == "Agent", onClick = { userType = "Agent" }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                placeholder = { Text("Enter your full name") },
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Name", tint = Color(0xFF6B7280)) },
                trailingIcon = {
                    if (fullName.isNotEmpty()) {
                        IconButton(onClick = { fullName = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF9CA3AF))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB), focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("Enter your email") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email", tint = Color(0xFF6B7280)) },
                trailingIcon = {
                    if (email.isNotEmpty()) {
                        Icon(
                            imageVector = if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB), focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Phone
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.length <= 15) phoneNumber = it },
                label = { Text("Phone Number") },
                placeholder = { Text("Enter your phone number") },
                leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone", tint = Color(0xFF6B7280)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB), focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Create a password") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF6B7280)) },
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
                    unfocusedBorderColor = Color(0xFFE5E7EB), focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            )

            // Password Strength Indicator
            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Password strength:", fontSize = 12.sp, color = Color(0xFF6B7280))
                    Text(
                        text = passwordStrength,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (passwordStrength) {
                            "Weak" -> Color(0xFFEF4444)
                            "Medium" -> Color(0xFFF59E0B)
                            "Strong" -> Color(0xFF10B981)
                            else -> Color(0xFF6B7280)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Re-enter your password") },
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Confirm Password", tint = Color(0xFF6B7280)) },
                trailingIcon = {
                    Row {
                        if (confirmPassword.isNotEmpty()) {
                            Icon(
                                imageVector = if (passwordMatch) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (passwordMatch) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Check else Icons.Default.Clear,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB), focusedBorderColor = Color(0xFF2563EB),
                    unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Terms and Conditions
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { agreeToTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB), uncheckedColor = Color(0xFF9CA3AF))
                )
                val termsText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 13.sp)) { append("I agree to the ") }
                    withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontSize = 13.sp, fontWeight = FontWeight.Medium)) { append("Terms of Service") }
                    withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 13.sp)) { append(" and ") }
                    withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontSize = 13.sp, fontWeight = FontWeight.Medium)) { append("Privacy Policy") }
                }
                Text(
                    text = termsText,
                    modifier = Modifier.padding(start = 4.dp, top = 12.dp).clickable { },
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button — triggers ViewModel
            Button(
                onClick = {
                    signUpPressed = true
                    logUpViewModel.logUp(email = email, name = fullName, password = password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(if (signUpPressed) 0.97f else 1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
                        && fullName.isNotEmpty()
                        && email.isNotEmpty()
                        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && password.isNotEmpty()
                        && passwordMatch
                        && agreeToTerms
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = "Create Account", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
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

            SocialSignUpButton(
                text = "Sign up with Google",
                icon = Icons.Default.AccountCircle,
                backgroundColor = Color.White,
                textColor = Color(0xFF1F2937),
                onClick = { /* Handle Google sign up */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialSignUpButton(
                text = "Sign up with Apple",
                icon = Icons.Default.DateRange,
                backgroundColor = Color(0xFF1F2937),
                textColor = Color.White,
                onClick = { /* Handle Apple sign up */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            val loginText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF6B7280), fontSize = 14.sp)) { append("Already have an account? ") }
                withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) { append("Login") }
            }
            Text(
                text = loginText,
                modifier = Modifier.clickable { navController.navigate(Screens.LoginScreenRoute) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UserTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        modifier = modifier.height(40.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF2563EB),
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color(0xFF6B7280)
        )
    )
}

@Composable
fun SocialSignUpButton(
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
        colors = ButtonDefaults.outlinedButtonColors(containerColor = backgroundColor, contentColor = textColor),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(Color(0xFFE5E7EB)))
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    MaterialTheme { }
}