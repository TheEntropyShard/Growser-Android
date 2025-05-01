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

package me.theentropyshard.growser.ui.gemini

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import me.theentropyshard.growser.LocalSettingsRepository
import me.theentropyshard.growser.gemini.text.document.GemtextBlockquoteElement
import me.theentropyshard.growser.gemini.text.document.GemtextElement
import me.theentropyshard.growser.gemini.text.document.GemtextH1Element
import me.theentropyshard.growser.gemini.text.document.GemtextHeaderElement
import me.theentropyshard.growser.gemini.text.document.GemtextLinkElement
import me.theentropyshard.growser.gemini.text.document.GemtextListElement
import me.theentropyshard.growser.gemini.text.document.GemtextPreformattedElement
import me.theentropyshard.growser.gemini.text.document.GemtextTextElement
import kotlin.math.roundToInt

@Composable
fun GemtextView(
    modifier: Modifier = Modifier,
    elements: List<GemtextElement>,
    scrollState: ScrollState,
    onUrlClick: (String) -> Unit = {}
) {
    val offsets = remember { mutableStateMapOf<Int, Int>() }
    val settings = LocalSettingsRepository.current

    val showTableOfContents by settings.showTableOfContents.collectAsState(false)

    if (showTableOfContents) {
        TableOfContents(
            elements = elements,
            scrollState = scrollState,
            offsets = offsets
        )
    }

    for (element in elements) {
        when (element.type) {
            GemtextElement.Type.TEXT -> {
                GemtextParagraph(text = (element as GemtextTextElement).text)
            }

            GemtextElement.Type.LINK -> {
                val linkElement = (element as GemtextLinkElement)

                GemtextLink(
                    label = linkElement.label,
                    url = linkElement.link,
                    onClick = { onUrlClick(it) }
                )
            }

            GemtextElement.Type.LIST -> {
                GemtextList(items = (element as GemtextListElement).elements)
            }

            GemtextElement.Type.H1, GemtextElement.Type.H2, GemtextElement.Type.H3 -> {
                val header = (element as GemtextHeaderElement)

                GemtextHeader(
                    modifier = Modifier.onGloballyPositioned {
                        offsets[elements.indexOf(element)] = it.positionInParent().y.roundToInt()
                    },
                    text = header.text,
                    fontWeight = header.getFontWeight(),
                    fontSize = header.getFontSize()
                )
            }

            GemtextElement.Type.BLOCKQUOTE -> {
                GemtextBlockquote(text = (element as GemtextBlockquoteElement).text)
            }

            GemtextElement.Type.PREFORMATTED -> {
                val preformatted = (element as GemtextPreformattedElement)

                GemtextPreformatted(
                    caption = preformatted.caption,
                    text = preformatted.text
                )
            }

            null -> {}
        }
    }
}
