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

package me.theentropyshard.growser.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

val GrowserTopBarHeight = 54.dp

@Composable
fun GrowserTopBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    state: TextFieldState,
    onSearch: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onMenuItemClick: (Int) -> Unit = {}
) {
    val density = LocalDensity.current

    val darkTheme = isSystemInDarkTheme()

    var menuShown by remember { mutableStateOf(false) }

    var offsetX by remember {
        mutableStateOf(0.dp)
    }

    var parentWidth by remember {
        mutableIntStateOf(0)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(GrowserTopBarHeight)
            .onPlaced {
                parentWidth = it.size.width
            }
            .drawBehind {
                drawLine(
                    if (darkTheme) Color.DarkGray else Color.LightGray,
                    Offset(0f, size.height),
                    Offset(size.width, size.height),
                    1f
                )
            }
    ) {
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 3.dp, top = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = ""
                )
            }

            SearchField(
                modifier = Modifier.weight(1f),
                hint = "Enter Gemini address",
                state = state,
                onSearch = onSearch
            )

            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = ""
                )
            }

            IconButton(onClick = {
                menuShown = true
            }) {
                Icon(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = ""
                )
            }
        }

        DropdownMenu(
            expanded = menuShown,
            onDismissRequest = { menuShown = false },
            offset = DpOffset(offsetX, 0.dp),
            modifier = Modifier
                .padding(end = 50.dp)
                .onPlaced {
                    offsetX = with(density) { (parentWidth - it.size.width).toDp() }
                }
        ) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AddBox,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("New Tab")
                },
                onClick = {
                    menuShown = false
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("History")
                },
                onClick = {
                    menuShown = false
                    onMenuItemClick(1)
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.DownloadDone,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("Downloads")
                },
                onClick = {
                    menuShown = false
                    onMenuItemClick(2)
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("Bookmarks")
                },
                onClick = {
                    menuShown = false
                    onMenuItemClick(3)
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("Settings")
                },
                onClick = {
                    menuShown = false
                    onMenuItemClick(4)
                }
            )
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = ""
                    )
                },
                text = {
                    Text("About")
                },
                onClick = {
                    menuShown = false
                    onMenuItemClick(5)
                }
            )
        }
    }
}