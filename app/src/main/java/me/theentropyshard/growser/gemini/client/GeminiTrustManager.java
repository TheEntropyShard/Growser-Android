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

import android.annotation.SuppressLint;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@SuppressLint({"CustomX509TrustManager", "TrustAllX509TrustManager"})
public class GeminiTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
