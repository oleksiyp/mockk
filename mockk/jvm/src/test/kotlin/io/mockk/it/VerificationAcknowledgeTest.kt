package io.mockk.it

import io.mockk.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class VerificationAcknowledgeTest {
    class MockCls {
        fun op(a: Int) = a + 1
    }

    val mock = mockk<MockCls>()

    fun doCalls1() {
        every { mock.op(5) } returns 1
        every { mock.op(6) } returns 2
        every { mock.op(7) } returns 3

        assertEquals(1, mock.op(5))
        assertEquals(2, mock.op(6))
        assertEquals(3, mock.op(7))
    }


    fun doCalls2() {
        every { mock.op(0) } throws RuntimeException("test")
        every { mock.op(1) } returnsMany listOf(1, 2, 3)

        assertFailsWith(RuntimeException::class) {
            mock.op(0)
        }

        assertEquals(1, mock.op(1))
        assertEquals(2, mock.op(1))
        assertEquals(3, mock.op(1))
        assertEquals(3, mock.op(1))
    }

    @Test
    fun checkVerify() {
        doCalls1()

        verify {
            mock.op(6)
            mock.op(5)
        }

        excludeRecords {
            mock.op(7)
        }

        ackVerified(mock)
    }

    @Test
    fun checkVerifyPreset() {
        excludeRecords(current = false) {
            mock.op(7)
        }

        doCalls1()

        verify {
            mock.op(6)
            mock.op(5)
        }

        ackVerified(mock)
    }

    @Test
    fun checkVerifyNoCurrentExclusion() {
        doCalls1()

        verify {
            mock.op(6)
            mock.op(5)
        }

        excludeRecords(current = false) {
            mock.op(7)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun checkVerifyInverse1() {
        doCalls1()

        verify(inverse = true) {
            mock.op(6)
            mock.op(8)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun checkVerifyInverse2() {
        doCalls1()

        verify(inverse = true) {
            mock.op(4)
            mock.op(8)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun checkVerifyOrder1() {
        doCalls1()

        verifyOrder {
            mock.op(5)
            mock.op(7)
        }

        excludeRecords { mock.op(6) }
        ackVerified(mock)
    }

    @Test
    fun checkVerifyOrder2() {
        doCalls1()

        verifyOrder {
            mock.op(5)
            mock.op(6)
        }

        excludeRecords { mock.op(7) }
        ackVerified(mock)
    }

    @Test
    fun checkVerifyOrder3() {
        doCalls1()

        verifyOrder {
            mock.op(6)
            mock.op(7)
        }

        excludeRecords { mock.op(5) }
        ackVerified(mock)
    }

    @Test
    fun checkVerifyOrderInverse1() {
        doCalls1()

        verifyOrder(inverse = true) {
            mock.op(7)
            mock.op(5)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun checkVerifyOrderInverse2() {
        doCalls1()

        verifyOrder(inverse = true) {
            mock.op(5)
            mock.op(4)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun checkVerifyOrderInverse3() {
        doCalls1()

        verifyOrder(inverse = true) {
            mock.op(4)
            mock.op(8)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun verifySequence() {
        doCalls1()

        verifySequence {
            mock.op(5)
            mock.op(6)
            mock.op(7)
        }

        ackVerified(mock)
    }

    @Test
    fun verifySequenceInverse1() {
        doCalls1()

        verifySequence(inverse = true) {
            mock.op(6)
            mock.op(7)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun verifySequenceInverse2() {
        doCalls1()

        verifySequence(inverse = true) {
            mock.op(7)
            mock.op(6)
            mock.op(5)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun verifySequenceInverse3() {
        doCalls1()

        verifySequence(inverse = true) {
            mock.op(6)
            mock.op(5)
            mock.op(7)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun atLeast() {
        doCalls2()

        verify(atLeast = 4) {
            mock.op(1)
        }

        excludeRecords { mock.op(0) }
        ackVerified(mock)
    }

    @Test
    fun atLeastInverse() {
        doCalls2()

        verify(atLeast = 5, inverse = true) {
            mock.op(1)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun exactly() {
        doCalls2()

        verify(exactly = 4) {
            mock.op(1)
        }

        excludeRecords { mock.op(0) }
        ackVerified(mock)
    }

    @Test
    fun exactlyInverse() {
        doCalls2()

        verify(exactly = 3, inverse = true) {
            mock.op(1)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun atMost() {
        doCalls2()

        verify(atMost = 4) {
            mock.op(1)
        }

        excludeRecords { mock.op(0) }
        ackVerified(mock)
    }

    @Test
    fun atMostInverse() {
        doCalls2()

        verify(atMost = 3, inverse = true) {
            mock.op(1)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun exactlyZero() {
        doCalls2()

        verify(exactly = 0) {
            mock.op(3)
        }

        assertFails {
            ackVerified(mock)
        }
    }


    @Test
    fun exactlyOnce() {
        doCalls2()

        verify(exactly = 1) {
            mock.op(0)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun exactlyTwiceInverse() {
        doCalls2()

        verify(exactly = 2, inverse = true) {
            mock.op(0)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun exactlyZeroInverse() {
        doCalls2()

        verify(exactly = 0, inverse = true) {
            mock.op(0)
        }

        assertFails {
            ackVerified(mock)
        }
    }

    @Test
    fun wasNotCalled() {
        val secondMock = mockk<MockCls>()
        val thirdMock = mockk<MockCls>()

        verify {
            listOf(secondMock, thirdMock) wasNot Called
        }

        ackVerified(secondMock, thirdMock)
    }

    @Test
    fun simple() {
        doCalls2()

        verify { mock.op(0) }

        excludeRecords { mock.op(1) }
        ackVerified(mock)
    }

    @Test
    fun order() {
        doCalls2()

        verifyOrder {
            mock.op(1)
            mock.op(1)
            mock.op(1)
            mock.op(1)
        }

        excludeRecords { mock.op(0) }
        ackVerified(mock)
    }

    @Test
    fun sequence() {
        doCalls2()

        verifySequence {
            mock.op(0)
            mock.op(1)
            mock.op(1)
            mock.op(1)
            mock.op(1)
        }

        ackVerified(mock)
    }
}

