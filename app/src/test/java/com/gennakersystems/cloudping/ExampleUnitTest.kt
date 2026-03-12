package com.gennakersystems.cloudping

import org.junit.Test
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        // Reference a class from 'main' to satisfy the dependency inspection
        val dummyState = PingUiState()
        assertNotNull(dummyState)
        assertEquals(4, 2 + 2)
    }
}
