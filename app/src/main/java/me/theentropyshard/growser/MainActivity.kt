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

package me.theentropyshard.growser

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewmodel.compose.viewModel
import me.theentropyshard.growser.settings.SettingsRepository
import me.theentropyshard.growser.ui.Growser
import me.theentropyshard.growser.ui.theme.GrowserTheme
import me.theentropyshard.growser.viewmodel.MainViewModel

val LocalSettingsRepository = compositionLocalOf<SettingsRepository> {
    error("No settings repository provided")
}

class MainActivity : ComponentActivity() {
    companion object {
        var firstUri: String = ""
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val repository = SettingsRepository(applicationContext)

            GrowserTheme {
                @Suppress("DEPRECATION")
                window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()

                CompositionLocalProvider(
                    LocalSettingsRepository provides repository
                ) {
                    Growser(uri = intent.data)
                }
            }
        }
    }

    /*override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        handleIntent(intent)
        Log.d(null, "WAS CALED HERE!!!")
    }*/
}
