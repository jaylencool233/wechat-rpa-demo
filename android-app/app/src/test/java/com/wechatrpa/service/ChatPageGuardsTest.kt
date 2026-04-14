package com.wechatrpa.service

import com.wechatrpa.model.AppTarget
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatPageGuardsTest {

    @Test
    fun `wework search page is not treated as chat page when only edit text exists`() {
        assertFalse(
            ChatPageGuards.isWeworkChatPage(
                hasChatTitle = false,
                hasChatInput = false,
                hasSendButton = false,
            ),
        )
    }

    @Test
    fun `wework chat page can be confirmed by title`() {
        assertTrue(
            ChatPageGuards.isWeworkChatPage(
                hasChatTitle = true,
                hasChatInput = false,
                hasSendButton = false,
            ),
        )
    }

    @Test
    fun `wework chat page can be confirmed by input and send button together`() {
        assertTrue(
            ChatPageGuards.isWeworkChatPage(
                hasChatTitle = false,
                hasChatInput = true,
                hasSendButton = true,
            ),
        )
    }

    @Test
    fun `open chat requires matching title after page transition`() {
        assertTrue(
            ChatPageGuards.canConfirmTargetChat(
                isChatPage = true,
                currentTitle = "纪嘉伦",
                expectedTitle = "纪嘉伦",
            ),
        )
        assertFalse(
            ChatPageGuards.canConfirmTargetChat(
                isChatPage = true,
                currentTitle = "搜索",
                expectedTitle = "纪嘉伦",
            ),
        )
    }

    @Test
    fun `wework message input fallback is disabled outside chat page`() {
        assertFalse(
            ChatPageGuards.allowGenericMessageInputFallback(
                target = AppTarget.WEWORK,
                isInChatPage = false,
            ),
        )
        assertTrue(
            ChatPageGuards.allowGenericMessageInputFallback(
                target = AppTarget.WEWORK,
                isInChatPage = true,
            ),
        )
        assertTrue(
            ChatPageGuards.allowGenericMessageInputFallback(
                target = AppTarget.WECHAT,
                isInChatPage = false,
            ),
        )
    }
}
