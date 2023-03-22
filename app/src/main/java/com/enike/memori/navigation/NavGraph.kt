package com.enike.memori.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.enike.memori.screens.AuthencicationViewModel
import com.enike.memori.screens.AuthenticationScreen
import com.enike.memori.utils.Constants.MEMORY_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import java.lang.Exception

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
        val viewModel: AuthencicationViewModel = viewModel()
        val googleAuthState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        AuthenticationScreen(
            loadingState = viewModel.isLoading.value,
            onButtonClicked = {
                googleAuthState.open()
            },
            googleAuthState = googleAuthState,
            messageBarState = messageBarState,
            onTokenIdRecieved = { token ->
                viewModel.signInWithMongoDBAtlas(
                    token,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated")
                        Log.d("Auth", token)
                    },
                    onError = { errorMessage ->
                        messageBarState.addError(
                            Exception(errorMessage)
                        )
                    })
            },
            onDialogDismissed = { errorMessage ->
                messageBarState.addError(
                    Exception(errorMessage)
                )
                Log.d("Auth", errorMessage)
            }
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