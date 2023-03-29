package com.enike.memori.navigation

import android.util.Log
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.enike.memori.components.DisplayAlertDialog
import com.enike.memori.screens.auth.AuthencicationViewModel
import com.enike.memori.screens.auth.AuthenticationScreen
import com.enike.memori.screens.home.HomeScreen
import com.enike.memori.utils.Constants.APP_ID
import com.enike.memori.utils.Constants.MEMORY_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

@Composable
fun SetUpNavigationGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(startDestination = startDestination, navController = navController) {
        authenticationRoute(
            navigateToHome = {
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToWriteScreen = {
                navController.navigate(Screen.Write.route)
            },
            navigateToAuthenticationScreen = {
                navController.navigate(Screen.Authentication.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit
) {
    composable(route = Screen.Authentication.route) {

        val viewModel: AuthencicationViewModel = viewModel()
        val authenticated by viewModel.isAuthenticated
        val googleAuthState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            isAuthencicated = authenticated,
            loadingState = viewModel.isLoading.value,
            onButtonClicked = {
                viewModel.setLoadingState(true)
                googleAuthState.open()
            },
            googleAuthState = googleAuthState,
            messageBarState = messageBarState,
            onTokenIdRecieved = { token ->
                viewModel.signInWithMongoDBAtlas(
                    token,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated")
                        viewModel.setLoadingState(false)
                    },
                    onError = { errorMessage ->
                        messageBarState.addError(
                            Exception(errorMessage)
                        )
                        viewModel.setLoadingState(false)
                    }
                )
            },
            onDialogDismissed = { errorMessage ->
                Log.d("Error", errorMessage)
                messageBarState.addError(
                    Exception(errorMessage)
                )
            },
            navigateToHome = navigateToHome
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(
    navigateToWriteScreen: () -> Unit,
    navigateToAuthenticationScreen: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        var signOutDialogeOpened by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        HomeScreen(
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                }
            },
            navigateToWriteScreen = navigateToWriteScreen,
            drawerState = drawerState,
            onSignOutClicked = {
                scope.launch {
                    signOutDialogeOpened = true
                }
            }
        )

        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to sign out?",
            dialogOpened = signOutDialogeOpened,
            onDialogClosed = {
                scope.launch {
                    signOutDialogeOpened = false
                }
            },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateToAuthenticationScreen()
                        }
                    }
                }
            }
        )

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