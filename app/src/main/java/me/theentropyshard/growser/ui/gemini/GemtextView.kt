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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.theentropyshard.growser.gemini.text.document.GemtextBlockquoteElement
import me.theentropyshard.growser.gemini.text.document.GemtextElement
import me.theentropyshard.growser.gemini.text.document.GemtextH1Element
import me.theentropyshard.growser.gemini.text.document.GemtextH2Element
import me.theentropyshard.growser.gemini.text.document.GemtextH3Element
import me.theentropyshard.growser.gemini.text.document.GemtextLinkElement
import me.theentropyshard.growser.gemini.text.document.GemtextListElement
import me.theentropyshard.growser.gemini.text.document.GemtextPreformattedElement
import me.theentropyshard.growser.gemini.text.document.GemtextTextElement

@Composable
fun GemtextView(
    modifier: Modifier = Modifier,
    elements: List<GemtextElement>,
    onUrlClick: (String) -> Unit = {}
) {
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

            GemtextElement.Type.H1 -> {
                GemtextH1(text = (element as GemtextH1Element).text)
            }

            GemtextElement.Type.H2 -> {
                GemtextH2(text = (element as GemtextH2Element).text)
            }

            GemtextElement.Type.H3 -> {
                GemtextH3(text = (element as GemtextH3Element).text)
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