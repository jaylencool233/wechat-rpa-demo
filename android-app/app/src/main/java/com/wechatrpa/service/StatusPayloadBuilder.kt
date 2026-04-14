package com.wechatrpa.service

import org.json.JSONObject

internal object StatusPayloadBuilder {
    const val BUILD_MARKER = "2026-04-15-status-marker-v1"

    fun build(
        accessibilityEnabled: Boolean,
        currentPackage: String,
        currentClass: String,
        taskQueueSize: Int,
    ): JSONObject {
        return JSONObject().apply {
            put("accessibility_enabled", accessibilityEnabled)
            put("current_package", currentPackage)
            put("current_class", currentClass)
            put("task_queue_size", taskQueueSize)
            put("http_server", true)
            put("build_marker", BUILD_MARKER)
        }
    }
}
