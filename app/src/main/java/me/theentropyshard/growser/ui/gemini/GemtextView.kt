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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.theentropyshard.growser.gemini.text.document.GemtextBlockquoteElement
import me.theentropyshard.growser.gemini.text.document.GemtextElement
import me.theentropyshard.growser.gemini.text.document.GemtextH1Element
import me.theentropyshard.growser.gemini.text.document.GemtextH2Element
import me.theentropyshard.growser.gemini.text.document.GemtextH3Element
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

    TableOfContents(
        elements = elements,
        scrollState = scrollState,
        offsets = offsets
    )

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
                GemtextH1(
                    modifier = Modifier.onGloballyPositioned {
                        offsets[elements.indexOf(element)] = it.positionInRoot().y.roundToInt()
                    },
                    text = (element as GemtextH1Element).text
                )
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

@Composable
fun TableOfContents(
    modifier: Modifier = Modifier,
    elements: List<GemtextElement>,
    scrollState: ScrollState,
    offsets: Map<Int, Int>
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column {
        Row(
            modifier = Modifier.clickable { expanded = !expanded }
        ) {
            Icon(
                modifier = Modifier.graphicsLayer {
                    rotationZ = if (expanded) 90f else 0f
                },
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = ""
            )

            Text(text = "Table Of Contents")
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 24.dp)) {
                for (element in elements) {
                    if (element is GemtextH1Element) {
                        Text(
                            modifier = Modifier.clickable {
                                scope.launch {
                                    offsets[elements.indexOf(element)]?.let {
                                        scrollState.animateScrollTo(
                                            it
                                        )
                                    }
                                }
                            },
                            text = element.text
                        )
                    }

                    if (element is GemtextH2Element) {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = element.text
                        )
                    }

                    if (element is GemtextH3Element) {
                        Text(
                            modifier = Modifier.padding(start = 32.dp),
                            text = element.text
                        )
                    }
                }
            }
        }
    }
}
