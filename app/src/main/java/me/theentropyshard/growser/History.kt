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

import java.util.ArrayDeque

class History {
    private val backStack = ArrayDeque<String>()
    private val forwardStack = ArrayDeque<String>()

    private var currentUri: String = ""

    fun visit(uri: String) {
        if (currentUri != "") {
            backStack.push(currentUri)
        }

        currentUri = uri
        forwardStack.clear()
    }

    fun back(): String {
        if (canNavigateBack()) {
            forwardStack.push(currentUri)
            currentUri = backStack.pop()
        }

        return currentUri
    }

    fun forward(): String {
        if (canNavigateForward()) {
            backStack.push(currentUri)
            currentUri = forwardStack.pop()
        }

        return currentUri
    }

    fun canNavigateBack() = backStack.isNotEmpty()

    fun canNavigateForward() = forwardStack.isNotEmpty()
}