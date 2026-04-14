package com.wechatrpa.service

import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import java.nio.charset.Charset

internal object RequestBodyParser {
    fun parseJsonBody(session: NanoHTTPD.IHTTPSession): JSONObject {
        val bodyText = readBodyText(session)
        return if (bodyText.isBlank()) JSONObject() else JSONObject(bodyText)
    }

    internal fun readBodyText(session: NanoHTTPD.IHTTPSession): String {
        val headers = session.headers
        val contentLength = headerValue(headers, "content-length")?.trim()?.toIntOrNull() ?: 0
        if (contentLength <= 0) {
            return ""
        }

        val buffer = ByteArray(contentLength)
        val input = session.inputStream
        var offset = 0
        while (offset < contentLength) {
            val read = input.read(buffer, offset, contentLength - offset)
            if (read < 0) {
                break
            }
            offset += read
        }

        if (offset <= 0) {
            return ""
        }

        val bodyBytes = if (offset == buffer.size) buffer else buffer.copyOf(offset)
        return decodeBody(bodyBytes, headerValue(headers, "content-type"))
    }

    internal fun decodeBody(bodyBytes: ByteArray, contentType: String?): String {
        if (bodyBytes.isEmpty()) {
            return ""
        }

        val charset = parseCharset(contentType) ?: Charsets.UTF_8
        return try {
            bodyBytes.toString(charset)
        } catch (_: Exception) {
            bodyBytes.toString(Charsets.UTF_8)
        }
    }

    private fun parseCharset(contentType: String?): Charset? {
        if (contentType.isNullOrBlank()) {
            return null
        }

        val charsetName = contentType
            .split(';')
            .map { it.trim() }
            .firstOrNull { it.startsWith("charset=", ignoreCase = true) }
            ?.substringAfter('=')
            ?.trim()
            ?.ifBlank { null }
            ?: return null

        return runCatching { Charset.forName(charsetName) }.getOrNull()
    }

    private fun headerValue(headers: Map<String, String>, name: String): String? {
        return headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value
    }
}
