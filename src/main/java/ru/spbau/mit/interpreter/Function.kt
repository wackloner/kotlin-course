package ru.spbau.mit.interpreter

import ru.spbau.mit.FunParser
import java.io.PrintStream

class Function(
        val name: String,
        private val parameterNames: List<String>,
        private val body: FunParser.BlockContext,
        private val frozenScope: Scope
) {
    init {
        frozenScope.defineFunction(this)
    }

    fun invoke(arguments: List<Int>, printStream: PrintStream): Int {
        FunException.assert(
                arguments.size != parameterNames.size,
                "Invalid number of arguments!"
        )
        val functionScope = Scope(frozenScope)
        parameterNames.forEachIndexed {
            index, name -> functionScope.initializeVariable(name, arguments[index])
        }

        val visitor = InterpretationVisitor(printStream, functionScope)
        return visitor.visit(body)
    }
}