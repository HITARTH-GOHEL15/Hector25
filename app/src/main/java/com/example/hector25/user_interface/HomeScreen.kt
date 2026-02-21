package com.example.hector25.user_interface

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hector25.user_interface.community.CommunityFeedScreen
import com.example.hector25.user_interface.dashBoard.DashboardScreen
import com.example.hector25.user_interface.profile.ProfileScreen
import com.example.hector25.user_interface.search.SearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController
) {
    var bottomNavigationScreens by rememberSaveable {
        mutableStateOf(BottomNavigationScreens.DashBoardView)
    }
    var isDashBoardScreen by remember {
        mutableStateOf(true)
    }

    Scaffold(

        topBar = {
            if (isDashBoardScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Hector25",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
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
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = bottomNavigationScreens == BottomNavigationScreens.DashBoardView,
                    onClick = {
                        bottomNavigationScreens = BottomNavigationScreens.DashBoardView
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text(
                            text = "Home",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2563EB),
                        selectedTextColor = Color(0xFF2563EB),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF),
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )

                NavigationBarItem(
                    selected = bottomNavigationScreens == BottomNavigationScreens.SearchView,
                    onClick = {
                        bottomNavigationScreens = BottomNavigationScreens.SearchView
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search"
                        )
                    },
                    label = {
                        Text(
                            text = "Search",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2563EB),
                        selectedTextColor = Color(0xFF2563EB),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF),
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )

                NavigationBarItem(
                    selected = bottomNavigationScreens == BottomNavigationScreens.CommunityView,
                    onClick = {
                        bottomNavigationScreens = BottomNavigationScreens.CommunityView
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Community"
                        )
                    },
                    label = {
                        Text(
                            text = "Community",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2563EB),
                        selectedTextColor = Color(0xFF2563EB),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF),
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )

                NavigationBarItem(
                    selected = bottomNavigationScreens == BottomNavigationScreens.ProfileView,
                    onClick = {
                        bottomNavigationScreens = BottomNavigationScreens.ProfileView
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text(
                            text = "Profile",
                            fontSize = 11.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF2563EB),
                        selectedTextColor = Color(0xFF2563EB),
                        unselectedIconColor = Color(0xFF9CA3AF),
                        unselectedTextColor = Color(0xFF9CA3AF),
                        indicatorColor = Color(0xFFEFF6FF)
                    )
                )
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) {
        AnimatedContent(
            targetState = bottomNavigationScreens,
            label = "",
            transitionSpec = {
                when(this.targetState) {
                    BottomNavigationScreens.DashBoardView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                    BottomNavigationScreens.SearchView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                    BottomNavigationScreens.CommunityView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                    BottomNavigationScreens.ProfileView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
                }
            },
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background)
        ) { navScreen ->
            when(navScreen) {
                BottomNavigationScreens.DashBoardView -> {
                    isDashBoardScreen = true
                    DashboardScreen(
                        navController
                    )
                }
                BottomNavigationScreens.SearchView -> {
                    isDashBoardScreen = false
                    SearchScreen()

                }
                BottomNavigationScreens.CommunityView -> {
                    isDashBoardScreen = false
                    CommunityFeedScreen()
                }
                BottomNavigationScreens.ProfileView -> {
                    isDashBoardScreen = false
                    ProfileScreen()
                }
            }
        }
    }
}

private enum class BottomNavigationScreens{
    DashBoardView,
    SearchView,
    CommunityView,
    ProfileView
}