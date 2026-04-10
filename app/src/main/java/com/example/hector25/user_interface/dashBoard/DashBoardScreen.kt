package com.example.hector25.user_interface.dashBoard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import org.koin.androidx.compose.koinViewModel

private val Blue600 = Color(0xFF2563EB)
private val Blue50  = Color(0xFFEFF6FF)
private val Gray900 = Color(0xFF111827)
private val Gray700 = Color(0xFF374151)
private val Gray500 = Color(0xFF6B7280)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray100 = Color(0xFFF3F4F6)
private val BgPage  = Color(0xFFF8F9FA)

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val buyState  by viewModel.buyState.collectAsState()
    val rentState by viewModel.rentState.collectAsState()

    var locationFilter by remember { mutableStateOf<String?>(null) }
    var priceFilter    by remember { mutableStateOf<String?>(null) }
    var sizeFilter     by remember { mutableStateOf<String?>(null) }
    var showLocationSheet by remember { mutableStateOf(false) }
    var showPriceSheet    by remember { mutableStateOf(false) }
    var showSizeSheet     by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { DashboardTopBar() },
        containerColor = BgPage
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 106.dp)
        ) {
            item {
                SearchSection(onSearchClick = {
                    navController.navigate(Screens.SearchScreenRoute)
                })
            }
            item {
                FilterChipsRow(
                    locationFilter  = locationFilter,
                    priceFilter     = priceFilter,
                    sizeFilter      = sizeFilter,
                    onLocationClick = { showLocationSheet = true },
                    onPriceClick    = { showPriceSheet = true },
                    onSizeClick     = { showSizeSheet = true },
                    onClearLocation = { locationFilter = null },
                    onClearPrice    = { priceFilter = null },
                    onClearSize     = { sizeFilter = null }
                )
            }
            item { YourSpaceSection(navController) }
            item {
                PropertySection(
                    title       = "Buy",
                    subtitle    = "Explore homes for sale",
                    state       = buyState,
                    navController = navController,
                    onRetry     = { viewModel.loadProperties() }
                )
            }
            item {
                PropertySection(
                    title       = "Rent",
                    subtitle    = "Find your next rental",
                    state       = rentState,
                    navController = navController,
                    onRetry     = { viewModel.loadProperties() }
                )
            }
            item {
                PropertySection(
                    title       = "New Launch",
                    subtitle    = "Fresh listings just added",
                    state       = buyState,
                    navController = navController,
                    onRetry     = { viewModel.loadProperties() }
                )
            }
        }
    }

    if (showLocationSheet) {
        LocationFilterSheet(
            current   = locationFilter,
            onSelect  = { locationFilter = it; showLocationSheet = false },
            onDismiss = { showLocationSheet = false }
        )
    }
    if (showPriceSheet) {
        PriceFilterSheet(
            current   = priceFilter,
            onSelect  = { priceFilter = it; showPriceSheet = false },
            onDismiss = { showPriceSheet = false }
        )
    }
    if (showSizeSheet) {
        SizeFilterSheet(
            current   = sizeFilter,
            onSelect  = { sizeFilter = it; showSizeSheet = false },
            onDismiss = { showSizeSheet = false }
        )
    }
}

// ─── Top bar ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar() {
    TopAppBar(
        title = {
            Column {
                Text("Hector25", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Gray900)
                Text("Find your dream property", fontSize = 12.sp, color = Gray500)
            }
        },
        actions = {
            IconButton(onClick = { }) {
                BadgedBox(badge = {
                    Badge(containerColor = Color(0xFFEF4444)) { Text("3", fontSize = 9.sp) }
                }) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Blue600)
                }
            }
            Spacer(Modifier.width(4.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        modifier = Modifier.shadow(2.dp)
    )
}

// ─── Search section ───────────────────────────────────────────────────────────
@Composable
fun SearchSection(onSearchClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable { onSearchClick() },
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFFF9FAFB),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Gray500, modifier = Modifier.size(20.dp))
                Text("Search by location, property, or more", color = Gray500, fontSize = 14.sp)
            }
        }
    }
}

// ─── Filter chips ─────────────────────────────────────────────────────────────
@Composable
fun FilterChipsRow(
    locationFilter: String?,
    priceFilter: String?,
    sizeFilter: String?,
    onLocationClick: () -> Unit,
    onPriceClick: () -> Unit,
    onSizeClick: () -> Unit,
    onClearLocation: () -> Unit,
    onClearPrice: () -> Unit,
    onClearSize: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ActiveFilterChip("Location", locationFilter != null, onLocationClick, onClearLocation)
        ActiveFilterChip("Price",    priceFilter != null,    onPriceClick,    onClearPrice)
        ActiveFilterChip("Size",     sizeFilter != null,     onSizeClick,     onClearSize)
    }
}

@Composable
fun ActiveFilterChip(label: String, isActive: Boolean, onClick: () -> Unit, onClear: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) Blue50 else Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) Blue600 else Gray300)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text  = label,
                fontSize = 13.sp,
                color = if (isActive) Blue600 else Gray700,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
            )
            if (isActive) {
                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Blue600,
                    modifier = Modifier.size(14.dp).clickable { onClear() })
            } else {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Gray500, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Your Space ───────────────────────────────────────────────────────────────
@Composable
fun YourSpaceSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        DashSectionHeader(title = "Your Space", onSeeAll = null)
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SpaceCard("Your Repository", "Manage your listed properties", "2 Properties",
                listOf(Color(0xFFDBEAFE), Color(0xFFEFF6FF)), Icons.Default.Home, Blue600) {}
            SpaceCard("Community", "Connect with other buyers & sellers", "123 Members",
                listOf(Color(0xFFD1FAE5), Color(0xFFECFDF5)), Icons.Default.Person, Color(0xFF059669)) {
                // navController.navigate(Screens.CommunityFeedScreenRoute)
            }
            SpaceCard("Agenda", "Upcoming site visits & events", "3 Events",
                listOf(Color(0xFFFEF3C7), Color(0xFFFFFBEB)), Icons.Default.DateRange, Color(0xFFD97706)) {}
        }
    }
}

@Composable
fun SpaceCard(
    title: String,
    subtitle: String,
    count: String,
    gradientColors: List<Color>,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(88.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(count, fontSize = 11.sp, color = Gray500)
                Spacer(Modifier.height(2.dp))
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Gray900)
                Text(subtitle, fontSize = 12.sp, color = Gray500, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = iconTint)
            }
        }
    }
}

// ─── Property section ─────────────────────────────────────────────────────────
@Composable
fun PropertySection(
    title: String,
    subtitle: String,
    state: PropertyUiState,
    navController: NavController,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        DashSectionHeader(
            title    = title,
            subtitle = subtitle,
            onSeeAll = { navController.navigate(Screens.SearchScreenRoute) },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        when (state) {
            is PropertyUiState.Loading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(3) { PropertyCardSkeleton() }
                }
            }
            is PropertyUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp)).background(Color(0xFFFEF2F2)).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                        Text(state.message, color = Color(0xFFEF4444), fontSize = 13.sp)
                        TextButton(onClick = onRetry) { Text("Retry", color = Blue600, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
            is PropertyUiState.Success -> {
                if (state.properties.isEmpty()) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("No properties found.", color = Gray500, fontSize = 13.sp)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.properties.size) { index ->
                            ApiPropertyCard(state.properties[index], navController)
                        }
                    }
                }
            }
        }
    }
}

// ─── Property card ────────────────────────────────────────────────────────────
@Composable
fun ApiPropertyCard(property: SimplyRetsProperty, navController: NavController) {
    val fallbackColors = listOf(
        Color(0xFFBFDBFE), Color(0xFFA5B4FC), Color(0xFFFED7AA),
        Color(0xFFD1D5DB), Color(0xFF93C5FD), Color(0xFFFBBF24)
    )
    val fallbackColor = fallbackColors[(property.mlsId ?: 0) % fallbackColors.size]
    val photoUrl   = property.photos?.firstOrNull()
    val streetLine = listOfNotNull(property.address?.streetNumber?.toString(), property.address?.streetName).joinToString(" ").ifEmpty { "Address unavailable" }
    val cityLine   = property.address?.city ?: ""
    val price      = property.listPrice?.let {} ?: "Price TBD"
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.width(210.dp).clickable {
            property.mlsId?.let { navController.navigate(Screens.PropertyDetailScreenRoute(mlsId = it)) }
        },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                if (photoUrl != null) {
                    AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(fallbackColor), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Home, null, modifier = Modifier.size(52.dp), tint = Color.White.copy(alpha = 0.6f))
                    }
                }
                // Favourite
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp).background(Color.White.copy(alpha = 0.9f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favourite",
                        tint = if (isFavorite) Color(0xFFEF4444) else Gray500,
                        modifier = Modifier.size(16.dp)
                    )
                }
                // Type badge
                property.property?.type?.let {
                    Surface(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp), color = Blue600.copy(alpha = 0.92f), shape = RoundedCornerShape(6.dp)) {
                        Text(it, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                // Status badge
                property.mls?.status?.let {
                    Surface(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp), color = Color(0xFF059669).copy(alpha = 0.92f), shape = RoundedCornerShape(6.dp)) {
                        Text(it, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = price.toString(), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Blue600)
                Spacer(Modifier.height(2.dp))
                Text(streetLine, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (cityLine.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.LocationOn, null, tint = Gray500, modifier = Modifier.size(12.dp))
                        Text(cityLine, fontSize = 11.sp, color = Gray500)
                    }
                }
                property.property?.let { prop ->
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = Gray100)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        prop.bedrooms?.let { if (it > 0) MiniStat("$it bd") }
                        prop.bathsFull?.let { MiniStat("$it ba") }
                        prop.area?.let { MiniStat("$it ft²") }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniStat(value: String) {
    Text(value, fontSize = 11.sp, color = Gray500)
}

// ─── Skeleton ─────────────────────────────────────────────────────────────────
@Composable
fun PropertyCardSkeleton() {
    Card(
        modifier = Modifier.width(210.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Gray100))
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.width(100.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).background(Gray100))
                Box(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(Gray100))
                Box(modifier = Modifier.fillMaxWidth(0.55f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(Gray100))
            }
        }
    }
}

// ─── Section header ───────────────────────────────────────────────────────────
@Composable
fun DashSectionHeader(
    title: String,
    subtitle: String? = null,
    onSeeAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
            subtitle?.let { Text(it, fontSize = 12.sp, color = Gray500) }
        }
        onSeeAll?.let {
            TextButton(onClick = it, contentPadding = PaddingValues(0.dp)) {
                Text("See all", fontSize = 13.sp, color = Blue600, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Default.KeyboardArrowRight, null, tint = Blue600, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ─── Filter bottom sheets ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationFilterSheet(current: String?, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val options = listOf("Downtown", "Beverly Hills", "Malibu", "West Side", "Santa Monica", "Financial District")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        FilterSheetContent("Select Location", options, current, onSelect, Icons.Default.LocationOn)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceFilterSheet(current: String?, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val options = listOf("Under \$300K", "\$300K – \$600K", "\$600K – \$1M", "\$1M – \$2M", "Above \$2M")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        FilterSheetContent("Price Range", options, current, onSelect, Icons.Default.Home)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeFilterSheet(current: String?, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    val options = listOf("Under 1,000 sqft", "1,000–2,000 sqft", "2,000–3,500 sqft", "3,500–5,000 sqft", "5,000+ sqft")
    ModalBottomSheet(onDismissRequest = onDismiss) {
        FilterSheetContent("Property Size", options, current, onSelect, Icons.Default.Home)
    }
}

@Composable
fun FilterSheetContent(title: String, options: List<String>, current: String?, onSelect: (String) -> Unit, icon: ImageVector) {
    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 32.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Gray900)
        Spacer(Modifier.height(16.dp))
        options.forEach { option ->
            val selected = current == option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelect(option) }
                    .background(if (selected) Blue50 else Color.Transparent)
                    .padding(vertical = 14.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = if (selected) Blue600 else Gray500, modifier = Modifier.size(18.dp))
                    Text(option, fontSize = 15.sp, color = if (selected) Blue600 else Gray700, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                }
                if (selected) Icon(Icons.Default.Check, null, tint = Blue600, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
