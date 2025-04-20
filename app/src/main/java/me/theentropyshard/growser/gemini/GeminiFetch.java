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

package me.theentropyshard.growser.gemini;

import java.io.IOException;

import me.theentropyshard.growser.gemini.client.GeminiClient;
import me.theentropyshard.growser.gemini.client.GeminiRequest;
import me.theentropyshard.growser.gemini.client.GeminiResponse;

public class GeminiFetch {
    private static final GeminiClient CLIENT = new GeminiClient();

    public static GeminiResponse fetchWebPage(String url) throws IOException {
        url = url.endsWith("/") ? url : url + "/";

        return GeminiFetch.CLIENT.send(new GeminiRequest(url));
    }
}
