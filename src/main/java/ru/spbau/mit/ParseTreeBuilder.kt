package ru.spbau.mit

import org.antlr.v4.runtime.ANTLRFileStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext

object ParseTreeBuilder {
    fun build(fileName: String): ParserRuleContext {
        val lexer = FunLexer(ANTLRFileStream(fileName))
        val parser = FunParser(CommonTokenStream(lexer))
        parser.buildParseTree = true
        return parser.block()
    }
}