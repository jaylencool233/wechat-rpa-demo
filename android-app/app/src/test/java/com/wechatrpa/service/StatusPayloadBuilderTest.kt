package com.wechatrpa.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusPayloadBuilderTest {

    @Test
    fun `build status payload includes build marker`() {
        val payload = StatusPayloadBuilder.build(
            accessibilityEnabled = true,
            currentPackage = "com.tencent.wework",
            currentClass = "com.tencent.wework.launch.WwMainActivity",
            taskQueueSize = 2,
        )

        assertEquals(StatusPayloadBuilder.BUILD_MARKER, payload.getString("build_marker"))
        assertTrue(payload.getBoolean("http_server"))
        assertEquals(2, payload.getInt("task_queue_size"))
    }
}
