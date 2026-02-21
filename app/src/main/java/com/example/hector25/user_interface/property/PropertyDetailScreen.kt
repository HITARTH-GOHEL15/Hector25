package com.example.hector25.user_interface.property

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun PropertyDetailScreen(
    navController: NavController
) {
    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item { PropertyImageSection() }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { PriceAndLocationSection() }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { AboutPropertySection() }
                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { AmenitiesSection() }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // Floating top bar
            PropertyTopBar(
                isFavorite = isFavorite,
                onBackClick = {
                    navController.popBackStack()
                },
                onFavoriteClick = { isFavorite = !isFavorite },
                onShareClick = { /* Handle share */ },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )

            // Contact Agent Button
            ContactAgentButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun PropertyTopBar(
    isFavorite: Boolean,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF1F2937)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF1F2937)
                )
            }

            IconButton(
                onClick = onShareClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = Color(0xFF1F2937)
                )
            }
        }
    }
}

@Composable
fun PropertyImageSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        // Property image placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD1D5DB)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
        }

        // Image indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .size(width = if (index == 0) 24.dp else 8.dp, height = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index == 0) Color.White else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
fun PriceAndLocationSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "1,200,000",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "234 Elm Street, Anytown, CA",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
        }
    }
}

@Composable
fun AboutPropertySection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "About this property",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "This stunning 3-bedroom, 2-bathroom apartment offers breathtaking city views and luxurious amenities. Located in the heart of Anytown, it's perfect for those seeking a vibrant urban lifestyle.",
                fontSize = 14.sp,
                color = Color(0xFF6B7280),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AmenitiesSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Amenities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AmenityItem(
                        icon = Icons.Default.Info,
                        title = "Swimming Pool",
                        iconColor = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFEFF6FF),
                        modifier = Modifier.weight(1f)
                    )

                    AmenityItem(
                        icon = Icons.Default.AccountCircle,
                        title = "Gym",
                        iconColor = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFEFF6FF),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AmenityItem(
                        icon = Icons.Default.LocationOn,
                        title = "Parking",
                        iconColor = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFEFF6FF),
                        modifier = Modifier.weight(1f)
                    )

                    AmenityItem(
                        icon = Icons.Default.Warning,
                        title = "Security",
                        iconColor = Color(0xFF3B82F6),
                        backgroundColor = Color(0xFFEFF6FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun AmenityItem(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1F2937)
        )
    }
}

@Composable
fun ContactAgentButton(modifier: Modifier = Modifier) {
    Button(
        onClick = { /* Handle contact agent */ },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Text(
            text = "Contact Agent",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PropertyDetailScreenPreview() {
    MaterialTheme {
    }
}