package com.enike.memori.navigation

import com.enike.memori.utils.Constants.MEMORY_SCREEN_ARGUMENT_KEY

sealed class Screen(val route: String) {
    object Authentication : Screen(route = "authentication_screen")
    object Home : Screen(route = "home_screen")
    object Write :
        Screen(route = "write_screen?$MEMORY_SCREEN_ARGUMENT_KEY={$MEMORY_SCREEN_ARGUMENT_KEY}") {
        fun passMemoryId(memoriId: String): String {
            return "write_screen?$MEMORY_SCREEN_ARGUMENT_KEY={$memoriId}"
        }
    }
}
