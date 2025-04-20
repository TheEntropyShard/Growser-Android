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

package me.theentropyshard.growser.gemini.client;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class GeminiResponse implements Closeable {
    private final int statusCode;
    private final String metaInfo;
    private final BufferedReader reader;

    public GeminiResponse(int statusCode, String metaInfo, BufferedReader reader) {
        this.statusCode = statusCode;
        this.metaInfo = metaInfo;
        this.reader = reader;
    }

    public static GeminiResponse readFrom(InputStream inputStream) throws IOException {
        byte[] rawCode = new byte[3];
        inputStream.read(rawCode);
        int code = (rawCode[0] - '0') * 10 + rawCode[1] - '0';

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 1024);

        return new GeminiResponse(code, reader.readLine(), reader);
    }

    public String readToString() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = this.reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        if (stringBuilder.length() >= 2) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getMetaInfo() {
        return this.metaInfo;
    }
}
