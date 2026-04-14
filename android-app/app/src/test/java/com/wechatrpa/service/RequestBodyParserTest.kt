package com.wechatrpa.service

import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException

class RequestBodyParserTest {

    @Test
    fun `normalizeBodyText repairs latin1 mojibake json`() {
        val correctJson = """{"contact":"纪嘉伦","message":"测试消息","app_type":"wework"}"""
        val mojibakeJson = String(correctJson.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)

        val normalized = RequestBodyParser.normalizeBodyText(mojibakeJson)

        assertEquals(correctJson, normalized)
    }

    @Test
    fun `normalizeBodyText keeps valid chinese json unchanged`() {
        val correctJson = """{"contact":"纪嘉伦","message":"测试消息","app_type":"wework"}"""

        val normalized = RequestBodyParser.normalizeBodyText(correctJson)

        assertEquals(correctJson, normalized)
    }

    @Test
    fun `parseJsonBody reads utf8 json from request stream`() {
        val body = """{"contact":"纪嘉伦","message":"测试消息","app_type":"wework"}"""
        val session = FakeSession(
            body = body.toByteArray(Charsets.UTF_8),
            headers = mapOf(
                "content-length" to body.toByteArray(Charsets.UTF_8).size.toString(),
                "content-type" to "application/json; charset=utf-8",
            ),
        )

        val result = RequestBodyParser.parseJsonBody(session)

        assertEquals("纪嘉伦", result.getString("contact"))
        assertEquals("测试消息", result.getString("message"))
        assertEquals("wework", result.getString("app_type"))
    }

    @Test
    fun `parseJsonBody returns empty object when request has no body`() {
        val session = FakeSession(
            body = ByteArray(0),
            headers = emptyMap(),
        )

        val result = RequestBodyParser.parseJsonBody(session)

        assertTrue(result.similar(JSONObject()))
    }

    private class FakeSession(
        body: ByteArray,
        private val headers: Map<String, String>,
    ) : NanoHTTPD.IHTTPSession {
        private val input = ByteArrayInputStream(body)

        override fun execute() {
            throw UnsupportedOperationException()
        }

        override fun getCookies(): NanoHTTPD.CookieHandler {
            throw UnsupportedOperationException()
        }

        override fun getHeaders(): MutableMap<String, String> {
            return headers.toMutableMap()
        }

        override fun getInputStream() = input

        override fun getMethod() = NanoHTTPD.Method.POST

        override fun getParms(): MutableMap<String, String> {
            return mutableMapOf()
        }

        override fun getParameters(): MutableMap<String, MutableList<String>> {
            return mutableMapOf()
        }

        override fun getQueryParameterString(): String? {
            return null
        }

        override fun getUri() = "/api/send_message"

        @Throws(IOException::class, NanoHTTPD.ResponseException::class)
        override fun parseBody(files: MutableMap<String, String>) {
            throw UnsupportedOperationException()
        }

        override fun getRemoteIpAddress() = "127.0.0.1"

        override fun getRemoteHostName() = "localhost"
    }
}
