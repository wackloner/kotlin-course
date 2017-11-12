package ru.spbau.mit

import kotlin.test.assertEquals
import org.junit.Test
import ru.spbau.mit.ast.ExampleFunAST
import ru.spbau.mit.ast.FunAST
import ru.spbau.mit.ast.FunASTBuilder

class ParsingTest {
    private fun getAST(fileName: String): FunAST {
        val tree = ParseTreeBuilder.build(fileName)
        val visitor = FunASTBuilder()
        return visitor.build(tree)
    }

    @Test
    fun testSimple() {
        assertEquals(
                ExampleFunAST.FIRST,
                getAST("src/test/funSources/simple.fun")
        )
    }

    @Test
    fun testFibonacci() {
        assertEquals(
                ExampleFunAST.SECOND,
                getAST("src/test/funSources/fibonacci.fun")
        )
    }
}
