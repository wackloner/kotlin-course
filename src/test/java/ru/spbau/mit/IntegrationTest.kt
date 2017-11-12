package ru.spbau.mit

import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import ru.spbau.mit.interpreter.FunException
import ru.spbau.mit.interpreter.InterpretationVisitor
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class IntegrationTest {
    private val byteStream = ByteArrayOutputStream()
    private val printStream = PrintStream(byteStream)
    private val visitor: InterpretationVisitor = InterpretationVisitor(printStream)

    @After
    fun tearDown() {
        visitor.scope.clear()
        byteStream.reset()
    }

    @Test
    fun testSimple() {
        test(
                "src/test/funSources/simple.fun",
                "5"
        )
    }

    @Test
    fun testFibonacci() {
        test(
                "src/test/funSources/fibonacci.fun",
                "1 1", "2 2", "3 3", "4 5", "5 8"
        )
    }

    @Test
    fun testNthPrime() {
        test(
                "src/test/funSources/nthprime.fun",
                "1 2", "2 3", "3 5", "4 7", "5 11"
        )
    }

    @Test(expected = FunException::class)
    fun testFail() {
        val parseTree = ParseTreeBuilder.build("src/test/funSources/fail.fun")
        visitor.visit(parseTree)
    }

    private fun test(fileName: String, vararg expectedLines: String) {
        val parseTree = ParseTreeBuilder.build(fileName)
        visitor.visit(parseTree)
        assertInputIs(expectedLines)
    }

    private fun assertInputIs(lines: Array<out String>) {
        val byteS = ByteArrayOutputStream()
        val printS = PrintStream(byteS)
        lines.forEach { printS.println(it) }
        assertArrayEquals(byteS.toByteArray(), byteStream.toByteArray())
    }
}
