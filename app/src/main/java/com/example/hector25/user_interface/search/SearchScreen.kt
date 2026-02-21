package com.example.hector25.user_interface.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class PropertySearchResult(
    val id: Int,
    val title: String,
    val location: String,
    val price: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val area: String,
    val imageColor: Color,
    val isFavorite: Boolean = false,
    val type: String
)

data class SearchCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedBottomNav by remember { mutableStateOf(1) }
    var selectedCategory by remember { mutableStateOf("All") }
    var showFilters by remember { mutableStateOf(false) }
    var viewType by remember { mutableStateOf("grid") } // grid or list

    val categories = remember {
        listOf(
            SearchCategory("All", Icons.Default.Home, Color(0xFF2563EB)),
            SearchCategory("House", Icons.Default.Home, Color(0xFF10B981)),
            SearchCategory("Apartment", Icons.Default.Home, Color(0xFFF59E0B)),
            SearchCategory("Villa", Icons.Default.Home, Color(0xFF8B5CF6)),
            SearchCategory("Office", Icons.Default.Home, Color(0xFFEF4444))
        )
    }

    val properties = remember {
        listOf(
            PropertySearchResult(1, "Modern Family House", "Downtown, CA", "$850,000", 4, 3, "2,500 sqft", Color(0xFFBFDBFE), false, "House"),
            PropertySearchResult(2, "Luxury Apartment", "Beverly Hills, CA", "$1,200,000", 3, 2, "1,800 sqft", Color(0xFFDDD6FE), false, "Apartment"),
            PropertySearchResult(3, "Cozy Studio", "West Side, CA", "$450,000", 1, 1, "650 sqft", Color(0xFFFED7AA), false, "Apartment"),
            PropertySearchResult(4, "Spacious Villa", "Malibu, CA", "$2,500,000", 5, 4, "4,200 sqft", Color(0xFFD1FAE5), false, "Villa"),
            PropertySearchResult(5, "Modern Office Space", "Financial District", "$950,000", 0, 2, "3,000 sqft", Color(0xFFFFE4E6), false, "Office"),
            PropertySearchResult(6, "Beach House", "Santa Monica, CA", "$1,800,000", 4, 3, "3,200 sqft", Color(0xFFC7D2FE), false, "House")
        )
    }

    val recentSearches = remember {
        listOf("3BHK in Downtown", "Apartments under 500K", "Villas in Malibu", "Studio apartments")
    }

    Scaffold(
        topBar = { SearchTopBar() },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar Section
            SearchBarSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onFilterClick = { showFilters = !showFilters },
                onViewTypeChange = { viewType = if (viewType == "grid") "list" else "grid" }
            )

            // Categories
            CategoriesSection(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // Results count and sort
            ResultsHeader(
                count = properties.size,
                viewType = viewType
            )

            // Search Results
            if (searchQuery.isEmpty()) {
                RecentSearchesSection(recentSearches = recentSearches)
            } else {
                if (viewType == "grid") {
                    PropertyGridResults(properties = properties)
                } else {
                    PropertyListResults(properties = properties)
                }
            }
        }

        // Filter Bottom Sheet
        if (showFilters) {
            FilterBottomSheet(
                onDismiss = { showFilters = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Search Properties",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
        },
        actions = {
            IconButton(onClick = { /* Handle saved searches */ }) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Saved Searches",
                    tint = Color(0xFF4B5563)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun SearchBarSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onViewTypeChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search location, property...",
                    fontSize = 14.sp,
                    color = Color(0xFF9CA3AF)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF6B7280)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }
            },
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedContainerColor = Color.White
            ),
            singleLine = true
        )

        // Filter Button
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFF2563EB), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Filters",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CategoriesSection(
    categories: List<SearchCategory>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            CategoryChip(
                category = category,
                isSelected = selectedCategory == category.name,
                onClick = { onCategorySelected(category.name) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: SearchCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = if (isSelected) Color.White else category.color
                )
                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = category.color,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF9FAFB),
            labelColor = Color(0xFF6B7280)
        )
    )
}

@Composable
fun ResultsHeader(count: Int, viewType: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$count Properties Found",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1F2937)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sort by",
                fontSize = 14.sp,
                color = Color(0xFF6B7280)
            )
            Text(
                text = "Price",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2563EB),
                modifier = Modifier.clickable { /* Handle sort */ }
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Sort",
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun RecentSearchesSection(recentSearches: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Searches",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            TextButton(onClick = { /* Clear all */ }) {
                Text(
                    text = "Clear All",
                    fontSize = 14.sp,
                    color = Color(0xFF2563EB)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        recentSearches.forEach { search ->
            RecentSearchItem(search = search)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecentSearchItem(search: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { /* Handle search click */ }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //history
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = search,
            fontSize = 14.sp,
            color = Color(0xFF4B5563),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Use search",
            tint = Color(0xFF9CA3AF),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun PropertyGridResults(properties: List<PropertySearchResult>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(properties) { property ->
            PropertyGridCard(property = property)
        }
    }
}

@Composable
fun PropertyGridCard(property: PropertySearchResult) {
    var isFavorite by remember { mutableStateOf(property.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle property click */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Property Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(property.imageColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(50.dp)
                    )
                }

                // Favorite Button
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Type Badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp),
                    color = Color(0xFF2563EB),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = property.type,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Property Details
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = property.price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = property.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = property.location,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (property.bedrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Home,
                            value = "${property.bedrooms}"
                        )
                    }
                    PropertyFeature(
                        icon = Icons.Default.Home,
                        value = "${property.bathrooms}"
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyListResults(properties: List<PropertySearchResult>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(properties) { property ->
            PropertyListCard(property = property)
        }
    }
}

@Composable
fun PropertyListCard(property: PropertySearchResult) {
    var isFavorite by remember { mutableStateOf(property.isFavorite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle property click */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Property Image
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(property.imageColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp),
                    color = Color(0xFF2563EB),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = property.type,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            // Property Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = property.price,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = property.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = property.location,
                        fontSize = 13.sp,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (property.bedrooms > 0) {
                        PropertyFeature(
                            icon = Icons.Default.Home,
                            value = "${property.bedrooms} Beds"
                        )
                    }
                    PropertyFeature(
                        icon = Icons.Default.Home,
                        value = "${property.bathrooms} Baths"
                    )
                    PropertyFeature(
                        icon = Icons.Default.Home,
                        value = property.area
                    )
                }
            }
        }
    }
}

@Composable
fun PropertyFeature(
    icon: ImageVector,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

@Composable
fun FilterBottomSheet(onDismiss: () -> Unit) {
    var priceRange by remember { mutableStateOf(0f..1000000f) }
    var selectedBedrooms by remember { mutableStateOf("Any") }
    var selectedBathrooms by remember { mutableStateOf("Any") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clickable(enabled = false) { },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filters",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Price Range",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$${priceRange.start.toInt()} - $${priceRange.endInclusive.toInt()}",
                    fontSize = 14.sp,
                    color = Color(0xFF2563EB),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Bedrooms",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Any", "1", "2", "3", "4", "5+").forEach { bed ->
                        FilterOptionChip(
                            text = bed,
                            isSelected = selectedBedrooms == bed,
                            onClick = { selectedBedrooms = bed }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Bathrooms",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Any", "1", "2", "3", "4+").forEach { bath ->
                        FilterOptionChip(
                            text = bath,
                            isSelected = selectedBathrooms == bath,
                            onClick = { selectedBathrooms = bath }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Reset filters */ },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset")
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Filters")
                    }
                }
            }
        }
    }
}

@Composable
fun FilterOptionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 14.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF2563EB),
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF3F4F6),
            labelColor = Color(0xFF6B7280)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Color(0xFF2563EB) else Color(0xFFE5E7EB),
            borderWidth = 1.dp
        )
    )
}

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = {
                Icon(
                    imageVector = if (selectedIndex == 0) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text(text = "Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFEFF6FF)
            )
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = {
                Icon(
                    imageVector = if (selectedIndex == 1) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text(text = "Search", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFEFF6FF)
            )
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = {
                Icon(
                    imageVector = if (selectedIndex == 2) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Community"
                )
            },
            label = { Text(text = "Community", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFEFF6FF)
            )
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            icon = {
                Icon(
                    imageVector = if (selectedIndex == 3) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text(text = "Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                unselectedIconColor = Color(0xFF9CA3AF),
                unselectedTextColor = Color(0xFF9CA3AF),
                indicatorColor = Color(0xFFEFF6FF)
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        SearchScreen()
    }
}