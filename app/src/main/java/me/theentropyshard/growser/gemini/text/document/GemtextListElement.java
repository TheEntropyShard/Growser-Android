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

package me.theentropyshard.growser.gemini.text.document;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GemtextListElement extends GemtextElement implements Iterable<String> {
    private final List<String> elements;

    public GemtextListElement() {
        super(GemtextElement.Type.LIST);

        this.elements = new ArrayList<>();
    }

    public void addElement(String element) {
        this.elements.add(element);
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return this.elements.iterator();
    }

    public List<String> getElements() {
        return this.elements;
    }
}
