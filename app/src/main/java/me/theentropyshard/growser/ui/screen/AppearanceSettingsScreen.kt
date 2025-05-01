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

package me.theentropyshard.growser.ui.screen

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import me.theentropyshard.growser.LocalSettingsRepository
import me.theentropyshard.growser.settings.SettingsRepository

@Composable
fun AppearanceSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val settings = LocalSettingsRepository.current

    Column(modifier = modifier.fillMaxSize()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            var checked by remember { mutableStateOf(false) }

            SettingsItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.FormatPaint,
                        contentDescription = ""
                    )
                },
                text = "Enable dynamic theme",
                trailingContent = {
                    Switch(checked = checked, onCheckedChange = { checked = it })
                }
            )
        }

        val scope = rememberCoroutineScope()
        val showTableOfContents by settings.showTableOfContents.collectAsState(false)

        SettingsItem(
            text = "Show table of contents",
            trailingContent = {
                Switch(
                    checked = showTableOfContents,
                    onCheckedChange = {
                        scope.launch {
                            settings.saveShowTableOfContents(it)
                        }
                    }
                )
            }
        )
    }
}
