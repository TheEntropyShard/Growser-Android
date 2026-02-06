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
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.theentropyshard.growser.History
import me.theentropyshard.growser.gemini.GeminiFetch
import me.theentropyshard.growser.gemini.text.GemtextParser
import me.theentropyshard.growser.gemini.text.document.GemtextPage
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.net.URI
import java.nio.charset.StandardCharsets

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

    private var _currentPageText = MutableStateFlow("")
    val currentPageText = _currentPageText.asStateFlow()

    private var _statusCode = MutableStateFlow(0)
    val statusCode = _statusCode.asStateFlow()

    private var _statusLine = MutableStateFlow("")
    val statusLine = _statusLine.asStateFlow()

    private var _exception = MutableStateFlow("")
    val exception = _exception.asStateFlow()

    val history = History()

    private var currUrl: String = ""

    constructor(application: Application, uri: Uri? = null) : this(application) {
        if (uri != null) {
            if (uri.scheme == "gemini") {
                loadPage(uri.toString())
            } else if (uri.scheme == "content") {
                application.contentResolver.openInputStream(uri).use {
                    it?.bufferedReader()?.readText()?.let { text ->
                        setData(20, "text/gemini", text)
                    }
                }
            }
        }
    }

    fun loadPreviousPage() {
        this.loadPage(history.back(), false)
    }

    fun loadNextPage() {
        this.loadPage(history.forward(), false)
    }

    fun loadPage(url: String, addToHistory: Boolean = true) {
        if (url.trim().isEmpty()) {
            return
        }

        currUrl = if (!url.startsWith("gemini://")) {
            "gemini://$url"
        } else {
            url
        }

        val path = URI(currUrl).path

        if (path == null || path.isEmpty()) {
            currUrl = "$currUrl/"
        }

        if (addToHistory) history.visit(currUrl)
        _currentUrl.value = currUrl

        _pageState.value = PageState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                GeminiFetch.fetchWebPage(currUrl).use { response ->
                    _exception.value = ""

                    if (response.statusCode.toString().startsWith("3")) {
                        var redirectUrl = response.metaInfo

                        if (!redirectUrl.startsWith("gemini://")) {
                            redirectUrl = "gemini://${URI(currUrl).host}$redirectUrl"
                        }

                        loadPage(redirectUrl)

                        return@launch
                    }

                    setData(response.statusCode, response.metaInfo, response.readToString())
                }
            } catch (e: Exception) {
                _pageState.value = PageState.Ready
                Log.e("MainViewModel", "Could not fetch $currUrl", e)
                val writer = StringWriter()
                val stream = PrintWriter(writer)
                e.printStackTrace(stream)
                _exception.value = writer.toString()
            }
        }
    }

    private fun setData(statusCode: Int, metaInfo: String, gemtext: String) {
        _statusCode.value = statusCode
        _statusLine.value = metaInfo

        _currentPageText.value = gemtext

        val page = GemtextParser().parse(gemtext)

        _pageState.value = PageState.Ready

        _document.value = page
    }

    fun loadRelativePageToHost(relativeUrl: String) {
        val uri = URI(currUrl)

        val url = StringBuilder().apply {
            append("gemini://")
            append(uri.host)
            if (uri.port != -1) append(uri.port)
            append(relativeUrl)
        }.toString()

        this.loadPage(url)
    }

    fun loadRelativePageToPath(relativeUrl: String) {
        println("here? $relativeUrl")

        val uri = URI(currUrl)

        val split = uri.path.split("/")

        val path = if (split.last().contains(".")) {
            split.dropLast(1).joinToString(separator = "/")
        } else {
            uri.path
        }

        val url = StringBuilder().apply {
            append("gemini://")
            append(uri.host)
            if (uri.port != -1) append(uri.port)
            if (path == null || path.isEmpty()) {
                append("/")
            } else {
                append(path)
            }
            if (path != null && path.isNotEmpty() && !path.endsWith("/")) append("/")
            append(relativeUrl)
        }.toString()

        println(url)

        this.loadPage(url)
    }

    fun refresh() {
        this.loadPage(_currentUrl.value, false)
    }

    fun saveCurrentPageTo(context: Context, uri: Uri) {
        val contentResolver: ContentResolver = context.contentResolver
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                writer.write(_currentPageText.value)
            }
        }
    }
}