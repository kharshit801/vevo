// File: app/src/main/java/com/example/ecentrix/AssistantService.kt
package com.example.ecentrix

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class AssistantService : AccessibilityService() {

    companion object {
        private const val TAG = "AssistantService"
        private const val AMAZON_PACKAGE = "com.amazon.mShop.android.shopping"
        private const val PRODUCT_TITLE_ID = "com.amazon.mShop.android.shopping:id/item_title"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()

            if (packageName == AMAZON_PACKAGE) {
                Log.d(TAG, "Amazon app detected")
                extractProductTitle()
            }
        }
    }

    private fun extractProductTitle() {
        val rootNode = rootInActiveWindow ?: run {
            Log.w(TAG, "Root node is null")
            return
        }

        try {
            val titleNodes = rootNode.findAccessibilityNodeInfosByViewId(PRODUCT_TITLE_ID)

            if (titleNodes.isNotEmpty()) {
                val productTitle = titleNodes[0].text?.toString()
                if (!productTitle.isNullOrEmpty()) {
                    Log.i(TAG, "Product Title Detected: $productTitle")
                } else {
                    Log.d(TAG, "Product title node found but text is empty")
                }
            } else {
                Log.d(TAG, "No product title node found with ID: $PRODUCT_TITLE_ID")
            }

            titleNodes.forEach { it.recycle() }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting product title", e)
        } finally {
            rootNode.recycle()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Accessibility Service Connected")

        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                    AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            notificationTimeout = 100
        }

        serviceInfo = info
    }

    override fun onInterrupt() {
        Log.w(TAG, "Accessibility Service Interrupted")
    }
}