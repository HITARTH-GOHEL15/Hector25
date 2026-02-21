package com.example.hector25.user_interface.dashBoard



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hector25.navigation.Screens

@Composable
fun DashboardScreen(
    navController: NavController
) {
    Scaffold(
        topBar = { DashboardTopBar() },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 106.dp)
        ) {
            item { SearchSection() }
            item { FilterChips() }
            item { YourSpaceSection() }
            item { BuySection(navController) }
            item { RentSection(navController) }
            item { NewLaunchSection(navController) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Hector25",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },
        actions = {
            IconButton(onClick = { /* Handle notifications */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF2563EB)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun SearchSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = "Search by location, property, or more",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedContainerColor = Color(0xFFF9FAFB)
            )
        )
    }
}

@Composable
fun FilterChips() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(label = "Location", hasDropdown = true)
        FilterChip(label = "Price", hasDropdown = true)
        FilterChip(label = "Size", hasDropdown = true)
    }
}

@Composable
fun FilterChip(label: String, hasDropdown: Boolean) {
    var isSelected by remember { mutableStateOf(false) }

    FilterChip(
        selected = isSelected,
        onClick = { isSelected = !isSelected },
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp
                )
                if (hasDropdown) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFEFF6FF),
            selectedLabelColor = Color(0xFF2563EB),
            containerColor = Color.White,
            labelColor = Color(0xFF6B7280)
        ),
    )
}

@Composable
fun YourSpaceSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Your Space",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SpaceCard(
                title = "Your Repository",
                subtitle = "Manage your properties",
                count = "2 Properties",
                backgroundColor = Color(0xFFF0F9FF),
                icon = Icons.Default.Home
            )

            SpaceCard(
                title = "Community",
                subtitle = "Connect with others",
                count = "123 Members",
                backgroundColor = Color(0xFFDCFCE7),
                icon = Icons.Default.Person
            )

            SpaceCard(
                title = "Agenda",
                subtitle = "Upcoming events",
                count = "3 Events",
                backgroundColor = Color(0xFFFEF3C7),
                icon = Icons.Default.DateRange
            )
        }
    }
}

@Composable
fun SpaceCard(
    title: String,
    subtitle: String,
    count: String,
    backgroundColor: Color,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable {
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = count,
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF2563EB)
                )
            }
        }
    }
}

@Composable
fun BuySection(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Buy",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PropertyCard(
                    title = "Luxury Villa in Suburbia",
                    priceRange = "600,000 - 750,000",
                    imageColor = Color(0xFFBFDBFE),
                    navController = navController
                )
            }
            item {
                PropertyCard(
                    title = "Cozy Apartment",
                    priceRange = "200,000 - 350,000",
                    imageColor = Color(0xFFA5B4FC),
                    navController
                )
            }
        }
    }
}

@Composable
fun RentSection(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Rent",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PropertyCard(
                    title = "Studio Apartment in City Center",
                    priceRange = "1,500 - 2,000/month",
                    imageColor = Color(0xFFFED7AA),
                    navController
                )
            }
            item {
                PropertyCard(
                    title = "2-Bedroom Apartment",
                    priceRange = "2,500 - 3,000/month",
                    imageColor = Color(0xFFD1D5DB),
                    navController
                )
            }
        }
    }
}

@Composable
fun NewLaunchSection(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "New Launch",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PropertyCard(
                    title = "The Residences at Central Park",
                    priceRange = "Starting from 400,000",
                    imageColor = Color(0xFF93C5FD),
                    navController = navController
                )
            }
            item {
                PropertyCard(
                    title = "The Skyline Tower",
                    priceRange = "Starting from 800,000",
                    imageColor = Color(0xFFFBBF24),
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun PropertyCard(
    title: String,
    priceRange: String,
    imageColor: Color,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable {
                navController.navigate(Screens.PropertyDetailScreenRoute)
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(imageColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = priceRange,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {

    }
}