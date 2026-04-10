package com.example.hector25.navigation

import kotlinx.serialization.Serializable

sealed class Screens(val route: String) {
    @Serializable
    object HomeScreenRoute

    @Serializable
    object OnBoardingScreenRoute

    @Serializable
    object DashBoardScreenRoute

    @Serializable
    object CommunityFeedScreenRoute

    @Serializable
    object SearchScreenRoute

    @Serializable
    object ProfileScreenRoute

    @Serializable
    object LoginScreenRoute

    @Serializable
    object SignUpScreenRoute

    @Serializable
    data class PropertyDetailScreenRoute(val mlsId: Int)
}