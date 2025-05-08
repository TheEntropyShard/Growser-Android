/*
 * Growser - https://github.com/TheEntropyShard/Growser-Android
 * Copyright (C) 2025 TheEntropyShard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.growser.ui

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndSelectAll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.theentropyshard.growser.MainActivity
import me.theentropyshard.growser.ui.components.GrowserTopBar
import me.theentropyshard.growser.ui.components.MenuButton
import me.theentropyshard.growser.ui.gemini.ExceptionView
import me.theentropyshard.growser.ui.gemini.GemtextView
import me.theentropyshard.growser.ui.gemini.PermanentFailure
import me.theentropyshard.growser.ui.gemini.TemporaryFailure
import me.theentropyshard.growser.ui.screen.AppearanceSettingsScreen
import me.theentropyshard.growser.ui.screen.SettingsScreen
import me.theentropyshard.growser.viewmodel.MainViewModel
import me.theentropyshard.growser.viewmodel.MainViewModelFactory
import me.theentropyshard.growser.viewmodel.PageState

@Composable
fun Growser(uri: Uri? = null) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(factory = MainViewModelFactory(
        application = context.applicationContext as Application,
        uri = uri
    ))

    val exception by mainViewModel.exception.collectAsState()

    val pageState by mainViewModel.pageState.collectAsState()
    val currentUrl by mainViewModel.currentUrl.collectAsState()
    val page by mainViewModel.document.collectAsState()
    val currentPageText by mainViewModel.currentPageText.collectAsState()

    val statusCode by mainViewModel.statusCode.collectAsState()
    val statusLine by mainViewModel.statusLine.collectAsState()

    val navController = rememberNavController()

    BackHandler {
        mainViewModel.loadPreviousPage()
    }

    LaunchedEffect(Unit) {
        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
        if (MainActivity.firstUri.isNotEmpty()) {
            mainViewModel.loadPage(MainActivity.firstUri)
            MainActivity.firstUri = ""
        }
    }

    val state = remember(currentUrl) { TextFieldState(initialText = currentUrl) }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/gemini"),
        onResult = { selectedUri ->
            selectedUri?.let {
                mainViewModel.saveCurrentPageTo(context, it)
            }
        }
    )

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            NavHost(
                navController = navController,
                startDestination = "browser",
                enterTransition = {
                    if (initialState.destination.route == "browser") {
                        fadeIn(tween(250))
                    } else {
                        fadeIn(tween(250)) + slideInHorizontally { it / 2 }
                    }
                },
                exitTransition = {
                    if (initialState.destination.route == "browser") {
                        fadeOut(tween(200))
                    } else {
                        fadeOut(tween(200)) + slideOutHorizontally { -it / 2 }
                    }
                },
                popEnterTransition = {
                    if (targetState.destination.route == "browser") {
                        fadeIn(tween(250))
                    } else {
                        fadeIn(tween(250)) + slideInHorizontally { -it / 2 }
                    }
                },
                popExitTransition = {
                    if (targetState.destination.route == "browser") {
                        fadeOut(tween(200))
                    } else {
                        fadeOut(tween(200)) + slideOutHorizontally { it / 2 }
                    }
                },
            ) {
                composable("browser") {
                    Column {
                        GrowserTopBar(
                            modifier = Modifier
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        state.setTextAndSelectAll(state.text.toString())
                                    }
                                },
                            isLoading = pageState == PageState.Loading,
                            state = state,
                            onSearch = {
                                keyboardController?.hide()
                                mainViewModel.loadPage(it)
                            },
                            onHomeClick = {
                                mainViewModel.loadPage("geminiprotocol.net")
                            },
                            onRefreshClick = {
                                mainViewModel.refresh()
                            },
                            onMenuItemClick = { button ->
                                when (button) {
                                    MenuButton.NewTab -> {

                                    }

                                    MenuButton.SavePage -> {
                                        if (currentPageText.isEmpty()) {
                                            Toast.makeText(context, "Current page is empty!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            saveFileLauncher.launch("${page.title}.gmi")
                                        }
                                    }

                                    MenuButton.History -> {

                                    }

                                    MenuButton.Downloads -> {

                                    }

                                    MenuButton.Bookmarks -> {

                                    }

                                    MenuButton.Settings -> {
                                        navController.navigate("settings")
                                    }

                                    MenuButton.About -> {

                                    }
                                }
                            }
                        )
                        if (pageState == PageState.Ready) {
                            val digit = statusCode / 10

                            val scrollState = rememberScrollState()

                            SelectionContainer {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .verticalScroll(scrollState)
                                ) {
                                    if (exception.isNotEmpty()) {
                                        ExceptionView(
                                            message = "Could not fetch $currentUrl",
                                            stacktrace = exception
                                        )
                                    } else {
                                        if (digit == 2) {
                                            GemtextView(
                                                elements = page.elements,
                                                scrollState = scrollState,
                                                onUrlClick = {
                                                    if (it.startsWith("gemini://")) {
                                                        mainViewModel.loadPage(it)
                                                    } else {
                                                        if (it.startsWith("/")) {
                                                            mainViewModel.loadRelativePageToHost(it)
                                                        } else {
                                                            mainViewModel.loadRelativePageToPath(it)
                                                        }
                                                    }
                                                }
                                            )
                                        } else {
                                            when (digit) {
                                                1 -> {
                                                    Text(text = statusLine)
                                                }

                                                4 -> {
                                                    TemporaryFailure(
                                                        statusCode = statusCode,
                                                        statusLine = statusLine
                                                    )
                                                }

                                                5 -> {
                                                    PermanentFailure(
                                                        statusCode = statusCode,
                                                        statusLine = statusLine
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                composable("settings") {
                    SettingsScreen(navController = navController)
                }

                composable("settings/appearance") {
                    AppearanceSettingsScreen(
                        navController = navController
                    )
                }
            }
        }
    }
}
