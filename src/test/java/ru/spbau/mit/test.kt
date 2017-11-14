package ru.spbau.mit
import kotlin.test.assertEquals
import org.junit.Test

class TestSource {
    @Test
    fun firstSample() {
        val actual = solve(
                2,
                "bac",
                arrayOf(
                        Request(2, 'a'),
                        Request(1, 'b'),
                        Request(2, 'c')
                )
        )
        assertEquals("acb", actual)
    }

    @Test
    fun secondSample() {
        val actual = solve(
                1,
                "abacaba",
                arrayOf(
                        Request(1, 'a'),
                        Request(1, 'a'),
                        Request(1, 'c'),
                        Request(2, 'b')
                )
        )
        assertEquals("baa", actual)
    }

    @Test
    fun customTest() {
        val actual = solve(
                2000,
                "aaaaaaaaaa",
                Array(20000, { Request(1, 'a') })
        )
        assertEquals("", actual)
    }
}
