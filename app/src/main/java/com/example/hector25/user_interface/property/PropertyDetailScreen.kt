package com.example.hector25.user_interface.property

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.hector25.data.SimplyRetsProperty
import com.example.hector25.user_interface.dashBoard.PropertyUiState
import org.koin.androidx.compose.koinViewModel

private val Blue600   = Color(0xFF2563EB)
private val Blue50    = Color(0xFFEFF6FF)
private val Gray900   = Color(0xFF111827)
private val Gray700   = Color(0xFF374151)
private val Gray500   = Color(0xFF6B7280)
private val Gray100   = Color(0xFFF3F4F6)
private val BgPage    = Color(0xFFF8F9FA)

// ─── Root ─────────────────────────────────────────────────────────────────────
@Composable
fun PropertyDetailScreen(
    navController: NavController,
    mlsId: Int,
    viewModel: PropertyDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(mlsId) { viewModel.loadProperty(mlsId) }

    when (state) {
        is PropertyUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator(color = Blue600, modifier = Modifier.size(44.dp))
                    Text("Loading property…", color = Gray500, fontSize = 14.sp)
                }
            }
        }
        is PropertyUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Warning, null, tint = Color(0xFFEF4444), modifier = Modifier.size(48.dp))
                    Text((state as PropertyUiState.Error).message, color = Color(0xFFEF4444), fontSize = 14.sp)
                    Button(
                        onClick = { viewModel.loadProperty(mlsId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Retry") }
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Go back", color = Gray500)
                    }
                }
            }
        }
        is PropertyUiState.Success -> {
            val property = (state as PropertyUiState.Success).properties.firstOrNull()
            if (property != null) {
                PropertyDetailContent(property = property, navController = navController)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Property not found.", color = Gray500)
                }
            }
        }
    }
}

// ─── Main content ─────────────────────────────────────────────────────────────
@Composable
fun PropertyDetailContent(property: SimplyRetsProperty, navController: NavController) {
    var isFavorite    by remember { mutableStateOf(false) }
    var currentPhoto  by remember { mutableStateOf(0) }
    var showContactSheet by remember { mutableStateOf(false) }

    val address   = listOfNotNull(
        listOfNotNull(property.address?.streetNumber?.toString(), property.address?.streetName).joinToString(" ").ifEmpty { null },
        property.address?.city,
        property.address?.state
    ).joinToString(", ")
    val price     = property.listPrice?.let {  } ?: "Price TBD"
    val agentName = property.agent?.let { "${it.firstName ?: ""} ${it.lastName ?: ""}".trim() }.takeIf { !it.isNullOrBlank() } ?: "Agent"
    val photos    = property.photos?.takeIf { it.isNotEmpty() }

    Scaffold(containerColor = BgPage) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                // Photo gallery
                item {
                    PhotoGallerySection(
                        photos       = photos,
                        currentIndex = currentPhoto,
                        onIndexChange = { currentPhoto = it }
                    )
                }

                // Price + status row
                item {
                    PriceStatusRow(price = price.toString(), status = property.mls?.status, daysOnMarket = property.mls?.daysOnMarket)
                }

                // Address + quick stats
                item {
                    AddressAndStatsCard(address = address, property = property)
                }

                // About
                item { Spacer(Modifier.height(16.dp)) }
                item { AboutSection(remarks = property.remarks) }

                // Property details grid
                property.property?.let { prop ->
                    item { Spacer(Modifier.height(16.dp)) }
                    item { PropertyDetailsGrid(prop = prop) }
                }

                // Amenities
                item { Spacer(Modifier.height(16.dp)) }
                item { AmenitiesSection(property = property) }

                // Agent card
                property.agent?.let { agent ->
                    item { Spacer(Modifier.height(16.dp)) }
                    item {
                        AgentCard(
                            firstName  = agent.firstName,
                            lastName   = agent.lastName,
                            agentId    = agent.id,
                            onContact  = { showContactSheet = true }
                        )
                    }
                }

                // Similar photos strip (if more than 1)
                if ((photos?.size ?: 0) > 1) {
                    item { Spacer(Modifier.height(16.dp)) }
                    item { PhotoStripSection(photos = photos!!, currentIndex = currentPhoto, onSelect = { currentPhoto = it }) }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }

            // Floating top bar
            DetailTopBar(
                isFavorite      = isFavorite,
                onBack          = { navController.popBackStack() },
                onFavoriteClick = { isFavorite = !isFavorite },
                onShare         = { /* share intent */ },
                modifier        = Modifier.align(Alignment.TopCenter).zIndex(1f)
            )

            // CTA button
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.98f))))
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = { showContactSheet = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Contact $agentName", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Contact agent sheet
    if (showContactSheet) {
        ContactAgentSheet(
            agentName = agentName,
            agentId   = property.agent?.id,
            onDismiss = { showContactSheet = false }
        )
    }
}

// ─── Top bar ──────────────────────────────────────────────────────────────────
@Composable
fun DetailTopBar(
    isFavorite: Boolean,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White, CircleShape)) {
            Icon(Icons.Default.ArrowBack, "Back", tint = Gray900)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onShare, modifier = Modifier.size(40.dp).background(Color.White, CircleShape)) {
                Icon(Icons.Default.Share, "Share", tint = Gray900)
            }
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(40.dp).background(Color.White, CircleShape)) {
                Icon(
                    if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    "Favourite",
                    tint = if (isFavorite) Color(0xFFEF4444) else Gray900
                )
            }
        }
    }
}

// ─── Photo gallery ────────────────────────────────────────────────────────────
@Composable
fun PhotoGallerySection(photos: List<String>?, currentIndex: Int, onIndexChange: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(330.dp)) {
        if (!photos.isNullOrEmpty()) {
            AsyncImage(
                model          = photos[currentIndex],
                contentDescription = null,
                modifier       = Modifier.fillMaxSize(),
                contentScale   = ContentScale.Crop
            )
            // Prev / Next arrows
            if (photos.size > 1) {
                IconButton(
                    onClick  = { if (currentIndex > 0) onIndexChange(currentIndex - 1) },
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp).size(36.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, "Previous", tint = Gray900)
                }
                IconButton(
                    onClick  = { if (currentIndex < photos.size - 1) onIndexChange(currentIndex + 1) },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(36.dp).background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, "Next", tint = Gray900)
                }
                // Photo counter
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    color    = Color.Black.copy(alpha = 0.55f),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text     = "${currentIndex + 1} / ${photos.size}",
                        fontSize = 12.sp,
                        color    = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            // Dot indicator
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(minOf(photos.size, 6)) { i ->
                    val isActive = i == currentIndex
                    Box(
                        modifier = Modifier
                            .size(width = if (isActive) 20.dp else 7.dp, height = 7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isActive) Color.White else Color.White.copy(alpha = 0.5f))
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(listOf(Color(0xFFBFDBFE), Color(0xFF93C5FD)))
                ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, null, modifier = Modifier.size(80.dp), tint = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

// ─── Photo strip ──────────────────────────────────────────────────────────────
@Composable
fun PhotoStripSection(photos: List<String>, currentIndex: Int, onSelect: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("All Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.padding(bottom = 10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(photos.size) { i ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onSelect(i) }
                ) {
                    AsyncImage(model = photos[i], contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    if (i == currentIndex) {
                        Box(modifier = Modifier.fillMaxSize().background(Blue600.copy(alpha = 0.35f)))
                        Box(modifier = Modifier.size(4.dp).align(Alignment.BottomCenter).background(Blue600))
                    }
                }
            }
        }
    }
}

// ─── Price + status ───────────────────────────────────────────────────────────
@Composable
fun PriceStatusRow(price: String, status: String?, daysOnMarket: Int?) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(price, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Blue600)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            status?.let {
                Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(8.dp)) {
                    Text(it, fontSize = 12.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
            daysOnMarket?.let {
                Surface(color = Gray100, shape = RoundedCornerShape(8.dp)) {
                    Text("$it days", fontSize = 12.sp, color = Gray700, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
        }
    }
}

// ─── Address + stats ──────────────────────────────────────────────────────────
@Composable
fun AddressAndStatsCard(address: String, property: SimplyRetsProperty) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Address
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.LocationOn, null, tint = Blue600, modifier = Modifier.size(18.dp).padding(top = 2.dp))
                Text(address.ifEmpty { "Address unavailable" }, fontSize = 15.sp, color = Gray700, lineHeight = 22.sp)
            }
            // MLS area
            property.mls?.area?.let {
                Spacer(Modifier.height(4.dp))
                Text("Area: $it", fontSize = 13.sp, color = Gray500)
            }

            property.property?.let { prop ->
                if (listOfNotNull(prop.bedrooms, prop.bathsFull, prop.area, prop.stories).isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Gray100)
                    Spacer(Modifier.height(16.dp))
                    // Stats row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        prop.bedrooms?.let { if (it > 0) DetailStatItem(Icons.Default.Home, "$it", "Beds") }
                        prop.bathsFull?.let { DetailStatItem(Icons.Default.Home, "$it", "Baths") }
                        prop.bathsHalf?.let { if (it > 0) DetailStatItem(Icons.Default.Home, "$it", "Half Baths") }
                        prop.area?.let { DetailStatItem(Icons.Default.Home, "$it", "sqft") }
                        prop.stories?.let { if (it > 1) DetailStatItem(Icons.Default.Home, "$it", "Floors") }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailStatItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier.size(40.dp).background(Blue50, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Blue600, modifier = Modifier.size(20.dp))
        }
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Gray900)
        Text(label, fontSize = 11.sp, color = Gray500)
    }
}

// ─── About ────────────────────────────────────────────────────────────────────
@Composable
fun AboutSection(remarks: String?) {
    var expanded by remember { mutableStateOf(false) }
    val text = remarks?.takeIf { it.isNotBlank() } ?: "No description available for this property."

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("About this property", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.padding(bottom = 10.dp))
            Text(
                text       = text,
                fontSize   = 14.sp,
                color      = Gray700,
                lineHeight = 22.sp,
                maxLines   = if (expanded) Int.MAX_VALUE else 4,
                overflow   = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            if (text.length > 200) {
                TextButton(onClick = { expanded = !expanded }, contentPadding = PaddingValues(0.dp)) {
                    Text(if (expanded) "Show less" else "Read more", color = Blue600, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─── Property details grid ────────────────────────────────────────────────────
@Composable
fun PropertyDetailsGrid(prop: com.example.hector25.data.PropertyDetail) {
    val details = buildList {
        prop.type?.let  { add("Type" to it) }
        prop.style?.let { add("Style" to it) }
        prop.pool?.let  { add("Pool" to it) }
        prop.bedrooms?.let { add("Bedrooms" to "$it") }
        prop.bathsFull?.let { add("Full Baths" to "$it") }
        prop.bathsHalf?.let { if (it > 0) add("Half Baths" to "$it") }
        prop.area?.let { add("Area" to "$it sqft") }
        prop.stories?.let { add("Stories" to "$it") }
    }
    if (details.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Property Details", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.padding(bottom = 12.dp))
            details.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (label, value) ->
                        DetailInfoItem(label = label, value = value, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun DetailInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(10.dp)).background(Gray100).padding(12.dp)
    ) {
        Text(label, fontSize = 11.sp, color = Gray500, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 14.sp, color = Gray900, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Amenities ────────────────────────────────────────────────────────────────
data class AmenityData(val icon: ImageVector, val title: String, val color: Color, val bgColor: Color)

@Composable
fun AmenitiesSection(property: SimplyRetsProperty) {
    val amenities = buildList<AmenityData> {
        if (property.property?.pool != null)
            add(AmenityData(Icons.Default.Info, "Swimming Pool", Color(0xFF3B82F6), Color(0xFFEFF6FF)))
        if ((property.property?.area ?: 0) > 2000)
            add(AmenityData(Icons.Default.AccountCircle, "Gym", Color(0xFF10B981), Color(0xFFD1FAE5)))
        add(AmenityData(Icons.Default.LocationOn, "Parking", Color(0xFFF59E0B), Color(0xFFFEF3C7)))
        add(AmenityData(Icons.Default.Warning, "24/7 Security", Color(0xFFEF4444), Color(0xFFFFE4E6)))
        if ((property.property?.stories ?: 0) > 1)
            add(AmenityData(Icons.Default.Home, "Elevator", Color(0xFF8B5CF6), Color(0xFFEDE9FE)))
        property.mls?.area?.let {
            add(AmenityData(Icons.Default.Place, "Area: $it", Color(0xFF6B7280), Gray100))
        }
    }
    if (amenities.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Amenities", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.padding(bottom = 14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                amenities.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { amenity ->
                            AmenityChip(amenity = amenity, modifier = Modifier.weight(1f))
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityChip(amenity: AmenityData, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(amenity.bgColor).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.size(34.dp).background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(amenity.icon, null, tint = amenity.color, modifier = Modifier.size(18.dp))
        }
        Text(amenity.title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Gray900, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
    }
}

// ─── Agent card ───────────────────────────────────────────────────────────────
@Composable
fun AgentCard(firstName: String?, lastName: String?, agentId: String?, onContact: () -> Unit) {
    val fullName = listOfNotNull(firstName, lastName).joinToString(" ").ifEmpty { "Listing Agent" }
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(52.dp).background(Blue50, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = listOfNotNull(firstName?.firstOrNull(), lastName?.firstOrNull()).joinToString("").ifEmpty { "A" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blue600
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(fullName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Gray900)
                Text("Listing Agent", fontSize = 13.sp, color = Gray500)
                agentId?.let { Text("ID: $it", fontSize = 11.sp, color = Gray500) }
            }
            // Message + call buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick  = onContact,
                    modifier = Modifier.size(40.dp).background(Blue50, CircleShape)
                ) {
                    Icon(Icons.Default.Email, null, tint = Blue600, modifier = Modifier.size(18.dp))
                }
                IconButton(
                    onClick  = onContact,
                    modifier = Modifier.size(40.dp).background(Blue600, CircleShape)
                ) {
                    Icon(Icons.Default.Phone, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ─── Contact sheet ────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactAgentSheet(agentName: String, agentId: String?, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 40.dp)) {
            Text("Contact Agent", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Gray900)
            Spacer(Modifier.height(6.dp))
            Text("Reach out to $agentName about this property.", fontSize = 14.sp, color = Gray500)
            Spacer(Modifier.height(24.dp))
            // Call button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Call Agent", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(10.dp))
            // Message button
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Blue600)
            ) {
                Icon(Icons.Default.Email, null, tint = Blue600, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Send Message", color = Blue600, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            agentId?.let {
                Spacer(Modifier.height(12.dp))
                Text("Agent ID: $it", fontSize = 12.sp, color = Gray500, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

// ─── PropertyDetailViewModel ──────────────────────────────────────────────────
// (keep in PropertyDetailViewModel.kt — shown here for reference)
// class PropertyDetailViewModel(private val repository: PropertyRepository) : ViewModel() { … }

// ─── Helper ───────────────────────────────────────────────────────────────────
