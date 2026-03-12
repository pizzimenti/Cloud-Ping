@file:Suppress("SpellCheckingInspection")
package com.gennakersystems.cloudping

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Reference a class from 'main' to satisfy the dependency inspection
        val dummyState = PingUiState()
        assertNotNull(dummyState)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.gennakersystems.cloudping", appContext.packageName)
    }
}
