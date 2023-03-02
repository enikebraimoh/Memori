package com.enike.memori.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.enike.memori.screens.AuthenticationRoute
import com.enike.memori.utils.Constants.MEMORY_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@Composable
fun SetUpNavigationGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(startDestination = startDestination, navController = navController) {
        authenticationRoute()
        homeRoute()
        writeRoute()
    }

}

fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.Authentication.route) {
        val googleAuthState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        AuthenticationRoute(
            loadingState = googleAuthState.opened,
            onButtonClicked = {
                googleAuthState.open()
            },
            googleAuthState = googleAuthState,
            messageBarState = messageBarState
        )
    }

}

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {

    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = MEMORY_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }

}