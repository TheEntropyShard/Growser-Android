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

package me.theentropyshard.growser.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.theentropyshard.growser.History
import me.theentropyshard.growser.gemini.GeminiFetch
import me.theentropyshard.growser.gemini.text.GemtextParser
import me.theentropyshard.growser.gemini.text.document.GemtextPage
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter

enum class PageState {
    NotReady, Loading, Ready
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var _pageState = MutableStateFlow(PageState.NotReady)
    val pageState = _pageState.asStateFlow()

    private var _currentUrl = MutableStateFlow("")
    val currentUrl = _currentUrl.asStateFlow()

    private var _document = MutableStateFlow(GemtextPage("", listOf()))
    val document = _document.asStateFlow()

    private var _statusCode = MutableStateFlow(0)
    val statusCode = _statusCode.asStateFlow()

    private var _statusLine = MutableStateFlow("")
    val statusLine = _statusLine.asStateFlow()

    private var _exception = MutableStateFlow("")
    val exception = _exception.asStateFlow()

    private val history = History()

    fun loadPreviousPage() {
        this.loadPage(history.pop(this.currentUrl.value), false)
    }

    fun loadPage(url: String, addToHistory: Boolean = true) {
        var theUrl = url

        if (!url.startsWith("gemini://")) {
            theUrl = "gemini://$theUrl"
        }

        if (!theUrl.endsWith("/")) {
            theUrl = "$theUrl/"
        }

        if (addToHistory) history.visit(theUrl)

        _currentUrl.value = theUrl

        _pageState.value = PageState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                GeminiFetch.fetchWebPage(theUrl).use { response ->
                    _exception.value = ""

                    _statusCode.value = response.statusCode
                    _statusLine.value = response.metaInfo

                    val text = response.readToString()
                    val page = GemtextParser().parse(text)

                    _pageState.value = PageState.Ready

                    _document.value = page
                }
            } catch (e: Exception) {
                _pageState.value = PageState.Ready
                Log.e("MainViewModel", "Could not fetch $url", e)
                val writer = StringWriter()
                val stream = PrintWriter(writer)
                e.printStackTrace(stream)
                _exception.value = writer.toString()
            }
        }
    }

    fun loadRelativePage(url: String) {
        var theUrl = url

        theUrl = if (_currentUrl.value.endsWith("/") && url.startsWith("/")) {
            "${_currentUrl.value}${url.drop(1)}"
        } else {
            "${_currentUrl.value}$theUrl"
        }

        this.loadPage(theUrl)
    }

    fun refresh() {
        this.loadPage(_currentUrl.value, false)
    }
}