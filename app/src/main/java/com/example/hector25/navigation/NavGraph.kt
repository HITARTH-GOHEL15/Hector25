package com.example.hector25.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.hector25.MainActivity
import com.example.hector25.user_interface.Auth.LoginScreen
import com.example.hector25.user_interface.Auth.SignUpScreen
import com.example.hector25.user_interface.HomeScreen
import com.example.hector25.user_interface.community.CommunityFeedScreen
import com.example.hector25.user_interface.dashBoard.DashboardScreen
import com.example.hector25.user_interface.onBoarding.WelcomeOnboardingScreen
import com.example.hector25.user_interface.profile.ProfileHeader
import com.example.hector25.user_interface.profile.ProfileScreen
import com.example.hector25.user_interface.property.PropertyDetailScreen
import com.example.hector25.user_interface.search.SearchScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun NavGraph(
    context: MainActivity
) {
    val navController = rememberNavController()
    val currentUser = Firebase.auth.currentUser
    val start = if (currentUser != null) Screens.HomeScreenRoute else Screens.OnBoardingScreenRoute

    NavHost(
        navController = navController,
        startDestination = start,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(100)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(100)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(100)
            )
        }
    ) {
        composable<Screens.OnBoardingScreenRoute> {
            WelcomeOnboardingScreen(
                navController
            )
        }

        //Auth
        composable<Screens.LoginScreenRoute> {
            LoginScreen(
                navController = navController
            )
        }
        composable<Screens.SignUpScreenRoute> {
            SignUpScreen(
                navController
            )
        }

        composable<Screens.HomeScreenRoute> {
            HomeScreen(
                navController
            )
        }

        composable<Screens.DashBoardScreenRoute> {
            DashboardScreen(
                navController
            )
        }

        composable<Screens.SearchScreenRoute> {
            SearchScreen(
                navController = navController
            )
        }

        composable<Screens.CommunityFeedScreenRoute> {
            CommunityFeedScreen()
        }

        composable<Screens.ProfileScreenRoute> {
            ProfileScreen()
        }

         composable<Screens.PropertyDetailScreenRoute> { backStackEntry ->
     val route = backStackEntry.toRoute<Screens.PropertyDetailScreenRoute>()
     PropertyDetailScreen(
         navController = navController,
         mlsId = route.mlsId
     )
 }
    }
}