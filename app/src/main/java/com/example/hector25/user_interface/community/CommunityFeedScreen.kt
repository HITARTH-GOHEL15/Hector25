package com.example.hector25.user_interface.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatar: Color,
    val title: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val timeAgo: String,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

@Composable
fun CommunityFeedScreen() {
    var selectedTab by remember { mutableStateOf(0) }

    val posts = remember {
        listOf(
            Post(
                id = 1,
                authorName = "John Doe",
                authorAvatar = Color(0xFFFFB74D),
                title = "Looking for a spacious 3BHK in a prime location",
                content = "I'm in the market for a 3BHK apartment in a central area with good connectivity. My budget is around \$150,000. Any recommendations or leads would be greatly appreciated!",
                likes = 234,
                comments = 12,
                timeAgo = "2d"
            ),
            Post(
                id = 2,
                authorName = "Sarah Williams",
                authorAvatar = Color(0xFFFFD699),
                title = "Investment opportunity: Commercial property in a developing area",
                content = "I've come across a commercial property in a rapidly developing area. The ROI seems promising, but I'd love to hear opinions from fellow investors. Is anyone familiar with this location?",
                likes = 187,
                comments = 8,
                timeAgo = "1d"
            ),
            Post(
                id = 3,
                authorName = "Mike Johnson",
                authorAvatar = Color(0xFFFFCC80),
                title = "Tips for first-time home buyers",
                content = "Buying your first home can be daunting. Here are some essential tips and advice you'd give to someone entering the real estate market for the first time.",
                likes = 312,
                comments = 25,
                timeAgo = "3d"
            )
        )
    }

    Scaffold(
        topBar = { CommunityTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle create post */ },
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Post",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab row
            CommunityTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Posts list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 106.dp)
            ) {
                items(posts) { post ->
                    PostCard(post = post)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Community",
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
fun CommunityTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = Color(0xFF2563EB),
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                height = 3.dp,
                color = Color(0xFF2563EB)
            )
        }
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
                Text(
                    text = "For You",
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selectedContentColor = Color(0xFF2563EB),
            unselectedContentColor = Color(0xFF6B7280)
        )

        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
                Text(
                    text = "Following",
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selectedContentColor = Color(0xFF2563EB),
            unselectedContentColor = Color(0xFF6B7280)
        )

        Tab(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            text = {
                Text(
                    text = "Groups",
                    fontSize = 15.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.SemiBold else FontWeight.Normal
                )
            },
            selectedContentColor = Color(0xFF2563EB),
            unselectedContentColor = Color(0xFF6B7280)
        )
    }
}

@Composable
fun PostCard(post: Post) {
    var isLiked by remember { mutableStateOf(post.isLiked) }
    var isSaved by remember { mutableStateOf(post.isSaved) }
    var likeCount by remember { mutableStateOf(post.likes) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Post header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(post.authorAvatar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = post.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${post.likes} likes",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "·",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "${post.comments} comments",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = "·",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                            Text(
                                text = post.timeAgo,
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                }

                IconButton(
                    onClick = { isSaved = !isSaved },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Add else Icons.Outlined.Clear,
                        contentDescription = "Save",
                        tint = if (isSaved) Color(0xFF2563EB) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post content
            Text(
                text = post.content,
                fontSize = 14.sp,
                color = Color(0xFF4B5563),
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Post actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        isLiked = !isLiked
                        likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                    }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = likeCount.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Comment button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Handle comment */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = "Comment",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = post.comments.toString(),
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // Share button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Handle share */ }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Share",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CommunityFeedScreenPreview() {
    MaterialTheme {
        CommunityFeedScreen()
    }
}