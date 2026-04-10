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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hector25.data.SimplyRetsProperty
import com.example.hector25.navigation.Screens
import com.example.hector25.user_interface.dashBoard.PropertyUiState
import org.koin.androidx.compose.koinViewModel

private val Blue600 = Color(0xFF2563EB)
private val Blue50  = Color(0xFFEFF6FF)
private val Gray900 = Color(0xFF111827)
private val Gray700 = Color(0xFF374151)
private val Gray500 = Color(0xFF6B7280)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray100 = Color(0xFFF3F4F6)
private val BgPage  = Color(0xFFF8F9FA)

data class SearchCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

enum class SortOption(val label: String) {
    PRICE_ASC("Price: Low → High"),
    PRICE_DESC("Price: High → Low"),
    NEWEST("Newest First"),
    BEDS_DESC("Most Bedrooms")
}

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel(), navController: NavController) {
    var searchQuery    by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showFilters    by remember { mutableStateOf(false) }
    var viewType       by remember { mutableStateOf("grid") }   // "grid" | "list"
    var sortOption     by remember { mutableStateOf(SortOption.PRICE_ASC) }
    var showSortMenu   by remember { mutableStateOf(false) }

    // filter params
    var filterMinBeds  by remember { mutableStateOf<Int?>(null) }
    var filterMinPrice by remember { mutableStateOf<Int?>(null) }
    var filterMaxPrice by remember { mutableStateOf<Int?>(null) }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadAll() }

    val categories = remember {
        listOf(
            SearchCategory("All",       Icons.Default.Home,     Blue600),
            SearchCategory("House",     Icons.Default.Home,     Color(0xFF10B981)),
            SearchCategory("Apartment", Icons.Default.Home,     Color(0xFFF59E0B)),
            SearchCategory("Villa",     Icons.Default.Home,     Color(0xFF8B5CF6)),
            SearchCategory("Office",    Icons.Default.Home,     Color(0xFFEF4444))
        )
    }

    val recentSearches = remember {
        listOf("3BHK in Downtown", "Apartments under 500K", "Villas in Malibu", "Studio apartments")
    }

    // derived filtered + sorted list
    val displayProperties: List<SimplyRetsProperty> = remember(state, searchQuery, selectedCategory, sortOption) {
        val base = (state as? PropertyUiState.Success)?.properties ?: return@remember emptyList()

        var filtered = base
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase()
            filtered = filtered.filter { p ->
                val addr = "${p.address?.city} ${p.address?.streetName} ${p.address?.state}".lowercase()
                val type = p.property?.type?.lowercase() ?: ""
                q in addr || q in type
            }
        }
        if (selectedCategory != "All") {
            val cat = selectedCategory.lowercase()
            filtered = filtered.filter { it.property?.type?.lowercase()?.contains(cat) == true }
        }

        when (sortOption) {
            SortOption.PRICE_ASC  -> filtered.sortedBy { it.listPrice ?: Int.MAX_VALUE }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.listPrice ?: 0 }
            SortOption.NEWEST     -> filtered  // no date field available, keep original
            SortOption.BEDS_DESC  -> filtered.sortedByDescending { it.property?.bedrooms ?: 0 }
        }
    }

    val isSearching = searchQuery.isNotBlank() || selectedCategory != "All"

    Scaffold(
        topBar = { SearchTopBar() },
        containerColor = BgPage
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // Search bar + filter button
                SearchBarSection(
                    searchQuery      = searchQuery,
                    onQueryChange    = { searchQuery = it },
                    onFilterClick    = { showFilters = true },
                    onViewTypeChange = { viewType = if (viewType == "grid") "list" else "grid" },
                    viewType         = viewType,
                    hasActiveFilters = filterMinBeds != null || filterMinPrice != null || filterMaxPrice != null
                )

                // Category chips
                CategoryChipsRow(
                    categories       = categories,
                    selectedCategory = selectedCategory,
                    onSelect         = { selectedCategory = it }
                )

                when (state) {
                    is PropertyUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                CircularProgressIndicator(color = Blue600)
                                Text("Loading properties…", color = Gray500, fontSize = 14.sp)
                            }
                        }
                    }
                    is PropertyUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(48.dp))
                                Text((state as PropertyUiState.Error).message, color = Color(0xFFEF4444), fontSize = 14.sp)
                                Button(
                                    onClick = { viewModel.loadAll() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Try again") }
                            }
                        }
                    }
                    is PropertyUiState.Success -> {
                        // Results header
                        SearchResultsHeader(
                            count          = displayProperties.size,
                            viewType       = viewType,
                            sortOption     = sortOption,
                            showSortMenu   = showSortMenu,
                            onViewToggle   = { viewType = if (viewType == "grid") "list" else "grid" },
                            onSortClick    = { showSortMenu = true },
                            onSortSelected = { sortOption = it; showSortMenu = false },
                            onDismissSort  = { showSortMenu = false }
                        )

                        if (!isSearching) {
                            // Show recent searches when idle
                            RecentSearchesSection(
                                recentSearches = recentSearches,
                                onSearchClick  = { searchQuery = it }
                            )
                        } else if (displayProperties.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Search, null, tint = Gray300, modifier = Modifier.size(56.dp))
                                    Text("No properties found", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Gray700)
                                    Text("Try adjusting your search or filters", fontSize = 14.sp, color = Gray500)
                                    TextButton(onClick = { searchQuery = ""; selectedCategory = "All" }) {
                                        Text("Clear search", color = Blue600)
                                    }
                                }
                            }
                        } else {
                            if (viewType == "grid") {
                                SearchPropertyGrid(properties = displayProperties, navController)
                            } else {
                                SearchPropertyList(properties = displayProperties)
                            }
                        }
                    }
                }
            }

            // Filter sheet
            if (showFilters) {
                SearchFilterSheet(
                    currentMinBeds  = filterMinBeds,
                    currentMinPrice = filterMinPrice,
                    currentMaxPrice = filterMaxPrice,
                    onApply = { beds, minP, maxP ->
                        filterMinBeds  = beds
                        filterMinPrice = minP
                        filterMaxPrice = maxP
                        viewModel.search(minBeds = beds, minPrice = minP, maxPrice = maxP)
                        showFilters = false
                    },
                    onReset = {
                        filterMinBeds  = null
                        filterMinPrice = null
                        filterMaxPrice = null
                        viewModel.loadAll()
                        showFilters = false
                    },
                    onDismiss = { showFilters = false }
                )
            }
        }
    }
}

// ─── Top bar ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar() {
    TopAppBar(
        title = { Text("Search Properties", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gray900) },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Star, contentDescription = "Saved searches", tint = Gray700)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

// ─── Search bar ───────────────────────────────────────────────────────────────
@Composable
fun SearchBarSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onViewTypeChange: () -> Unit,
    viewType: String,
    hasActiveFilters: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value           = searchQuery,
            onValueChange   = onQueryChange,
            placeholder     = { Text("City, street, property type…", fontSize = 14.sp, color = Gray500) },
            leadingIcon     = { Icon(Icons.Default.Search, null, tint = Gray500) },
            trailingIcon    = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, null, tint = Gray500)
                    }
                }
            },
            modifier        = Modifier.weight(1f).height(56.dp),
            shape           = RoundedCornerShape(12.dp),
            singleLine      = true,
            colors          = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor    = Color(0xFFE5E7EB),
                focusedBorderColor      = Blue600,
                unfocusedContainerColor = Gray100,
                focusedContainerColor   = Color.White
            )
        )
        // Filter button with active-state indicator
        BadgedBox(
            badge = {
                if (hasActiveFilters) Badge(containerColor = Color(0xFFEF4444)) {}
            }
        ) {
            IconButton(
                onClick  = onFilterClick,
                modifier = Modifier.size(56.dp).background(Blue600, RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Filters", tint = Color.White)
            }
        }
    }
}

// ─── Category chips ───────────────────────────────────────────────────────────
@Composable
fun CategoryChipsRow(
    categories: List<SearchCategory>,
    selectedCategory: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            val selected = selectedCategory == cat.name
            FilterChip(
                selected = selected,
                onClick  = { onSelect(cat.name) },
                label = {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(cat.icon, null, modifier = Modifier.size(16.dp), tint = if (selected) Color.White else cat.color)
                        Text(cat.name, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = cat.color,
                    selectedLabelColor     = Color.White,
                    containerColor         = Gray100,
                    labelColor             = Gray700
                )
            )
        }
    }
}

// ─── Results header ───────────────────────────────────────────────────────────
@Composable
fun SearchResultsHeader(
    count: Int,
    viewType: String,
    sortOption: SortOption,
    showSortMenu: Boolean,
    onViewToggle: () -> Unit,
    onSortClick: () -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onDismissSort: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().background(BgPage).padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$count results", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Sort dropdown
            Box {
                TextButton(onClick = onSortClick, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    Icon(Icons.Default.List, null, tint = Blue600, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(sortOption.label, fontSize = 12.sp, color = Blue600, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = Blue600, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = showSortMenu, onDismissRequest = onDismissSort) {
                    SortOption.entries.forEach { opt ->
                        DropdownMenuItem(
                            text = {
                                Text(opt.label, fontSize = 14.sp, color = if (sortOption == opt) Blue600 else Gray700,
                                    fontWeight = if (sortOption == opt) FontWeight.SemiBold else FontWeight.Normal)
                            },
                            onClick = { onSortSelected(opt) },
                            trailingIcon = {
                                if (sortOption == opt) Icon(Icons.Default.Check, null, tint = Blue600, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }
            // View toggle
            IconButton(onClick = onViewToggle, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (viewType == "grid") Icons.Default.List else Icons.Default.Menu,
                    contentDescription = "Toggle view",
                    tint = Blue600,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Recent searches ──────────────────────────────────────────────────────────
@Composable
fun RecentSearchesSection(recentSearches: List<String>, onSearchClick: (String) -> Unit) {
    var searches by remember { mutableStateOf(recentSearches) }
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Searches", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Gray900)
            TextButton(onClick = { searches = emptyList() }) {
                Text("Clear All", fontSize = 13.sp, color = Blue600)
            }
        }
        Spacer(Modifier.height(10.dp))
        if (searches.isEmpty()) {
            Text("No recent searches.", color = Gray500, fontSize = 13.sp)
        } else {
            searches.forEach { search ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onSearchClick(search) }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Outlined.Search, null, tint = Gray500, modifier = Modifier.size(18.dp))
                    Text(search, fontSize = 14.sp, color = Gray700, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Search, contentDescription = "Use search", tint = Gray300, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ─── Grid view ────────────────────────────────────────────────────────────────
@Composable
fun SearchPropertyGrid(properties: List<SimplyRetsProperty> , navController: NavController) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(properties) { SearchPropertyGridCard(it , navController) }
    }
}

@Composable
fun SearchPropertyGridCard(property: SimplyRetsProperty,navController: NavController) {
    var isFavorite by remember { mutableStateOf(false) }
    val photoUrl  = property.photos?.firstOrNull()
    val price     = property.listPrice?.let {  } ?: "TBD"
    val title     = property.address?.streetName ?: "Property"
    val location  = listOfNotNull(property.address?.city, property.address?.state).joinToString(", ")
    val fallback  = searchFallbackColor(property.mlsId)

    Card(
        modifier = Modifier.fillMaxWidth().clickable {
            property.mlsId?.let { navController.navigate(Screens.PropertyDetailScreenRoute(mlsId = it)) }
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                if (photoUrl != null) {
                    AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(fallback), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                    }
                }
                // Favourite
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(30.dp).background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        null, tint = if (isFavorite) Color(0xFFEF4444) else Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                }
                // Type badge
                property.property?.type?.let {
                    Surface(modifier = Modifier.align(Alignment.BottomStart).padding(6.dp), color = Blue600.copy(alpha = 0.9f), shape = RoundedCornerShape(5.dp)) {
                        Text(it, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
                // MLS status
                property.mls?.status?.let {
                    Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp), color = Color(0xFF059669).copy(alpha = 0.9f), shape = RoundedCornerShape(5.dp)) {
                        Text(it, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(price.toString(), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Blue600)
                Spacer(Modifier.height(2.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (location.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Gray500, modifier = Modifier.size(11.dp))
                        Text(location, fontSize = 11.sp, color = Gray500, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                property.property?.let { prop ->
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        prop.bedrooms?.let { if (it > 0) Text("$it bd", fontSize = 11.sp, color = Gray500) }
                        prop.bathsFull?.let { Text("$it ba", fontSize = 11.sp, color = Gray500) }
                        prop.area?.let { Text("${it} ft²", fontSize = 11.sp, color = Gray500) }
                    }
                }
            }
        }
    }
}

// ─── List view ────────────────────────────────────────────────────────────────
@Composable
fun SearchPropertyList(properties: List<SimplyRetsProperty>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(properties) { SearchPropertyListCard(it) }
    }
}

@Composable
fun SearchPropertyListCard(property: SimplyRetsProperty) {
    var isFavorite by remember { mutableStateOf(false) }
    val photoUrl = property.photos?.firstOrNull()
    val price    = property.listPrice?.let {  } ?: "TBD"
    val title    = property.address?.streetName ?: "Property"
    val location = listOfNotNull(property.address?.city, property.address?.state).joinToString(", ")
    val fallback = searchFallbackColor(property.mlsId)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Thumbnail
            Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp))) {
                if (photoUrl != null) {
                    AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(fallback), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(36.dp))
                    }
                }
                property.property?.type?.let {
                    Surface(modifier = Modifier.align(Alignment.TopStart).padding(5.dp), color = Blue600.copy(alpha = 0.9f), shape = RoundedCornerShape(4.dp)) {
                        Text(it, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp))
                    }
                }
            }

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(price.toString(), fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Blue600)
                    IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            null, tint = if (isFavorite) Color(0xFFEF4444) else Gray500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (location.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Gray500, modifier = Modifier.size(12.dp))
                        Text(location, fontSize = 12.sp, color = Gray500)
                    }
                }
                property.mls?.status?.let {
                    Spacer(Modifier.height(2.dp))
                    Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                        Text(it, fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                property.property?.let { prop ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        prop.bedrooms?.let { if (it > 0) SearchFeature(Icons.Default.Home, "$it Beds") }
                        prop.bathsFull?.let { SearchFeature(Icons.Default.Home, "$it Baths") }
                        prop.area?.let { SearchFeature(Icons.Default.Home, "$it ft²") }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchFeature(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, tint = Gray500, modifier = Modifier.size(13.dp))
        Text(value, fontSize = 11.sp, color = Gray500)
    }
}

// ─── Filter sheet ─────────────────────────────────────────────────────────────
@Composable
fun SearchFilterSheet(
    currentMinBeds: Int?,
    currentMinPrice: Int?,
    currentMaxPrice: Int?,
    onApply: (Int?, Int?, Int?) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedBedrooms by remember { mutableStateOf(currentMinBeds?.toString() ?: "Any") }
    var minPriceText     by remember { mutableStateOf(currentMinPrice?.toString() ?: "") }
    var maxPriceText     by remember { mutableStateOf(currentMaxPrice?.toString() ?: "") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.72f)
                .clickable(enabled = false) { },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                // Handle
                Box(modifier = Modifier.align(Alignment.CenterHorizontally).width(40.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Gray300))
                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gray900)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = Gray700) }
                }

                Spacer(Modifier.height(20.dp))

                // Price range
                Text("Price Range", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = minPriceText,
                        onValueChange = { minPriceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Min \$") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue600)
                    )
                    OutlinedTextField(
                        value = maxPriceText,
                        onValueChange = { maxPriceText = it.filter { c -> c.isDigit() } },
                        label = { Text("Max \$") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue600)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Quick price presets
                Text("Quick Select", fontSize = 13.sp, color = Gray500, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Under 500K" to (null to 500000), "500K–1M" to (500000 to 1000000), "1M+" to (1000000 to null)).forEach { (label, range) ->
                        val (mn, mx) = range
                        val active = minPriceText == (mn?.toString() ?: "") && maxPriceText == (mx?.toString() ?: "")
                        Surface(
                            onClick = { minPriceText = mn?.toString() ?: ""; maxPriceText = mx?.toString() ?: "" },
                            shape = RoundedCornerShape(8.dp),
                            color = if (active) Blue600 else Gray100
                        ) {
                            Text(label, fontSize = 12.sp, color = if (active) Color.White else Gray700, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Bedrooms
                Text("Minimum Bedrooms", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Any", "1", "2", "3", "4", "5+").forEach { bed ->
                        val sel = selectedBedrooms == bed
                        Surface(
                            onClick = { selectedBedrooms = bed },
                            shape = RoundedCornerShape(10.dp),
                            color = if (sel) Blue600 else Gray100,
                            border = if (sel) null else androidx.compose.foundation.BorderStroke(1.dp, Gray300)
                        ) {
                            Text(bed, fontSize = 14.sp, color = if (sel) Color.White else Gray700,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Action buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { onReset() },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Gray300)
                    ) {
                        Text("Reset", color = Gray700, fontWeight = FontWeight.SemiBold)
                    }
                    Button(
                        onClick = {
                            val beds = if (selectedBedrooms == "Any") null else selectedBedrooms.replace("+", "").toIntOrNull()
                            onApply(beds, minPriceText.toIntOrNull(), maxPriceText.toIntOrNull())
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Apply", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun searchFallbackColor(mlsId: Int?): Color {
    val colors = listOf(Color(0xFFBFDBFE), Color(0xFFDDD6FE), Color(0xFFFED7AA), Color(0xFFD1FAE5), Color(0xFFFFE4E6))
    return colors[(mlsId ?: 0) % colors.size]
}