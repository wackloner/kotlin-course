package ru.spbau.mit.interpreter

import ru.spbau.mit.FunBaseVisitor
import ru.spbau.mit.FunParser
import java.io.PrintStream

class InterpretationVisitor(
        private val printStream: PrintStream = System.out,
        var scope: Scope = Scope()
) : FunBaseVisitor<Int>() {
    companion object {
        var returnValue: Int? = null
    }

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext) = ctx.text.toInt()


    override fun visitPrintlnFunctionCall(ctx: FunParser.PrintlnFunctionCallContext): Int {
        val arguments = ctx.arguments().expression()
        printStream.println(arguments.map { visit(it) }.joinToString(" "))
        return 0
    }

    override fun visitVariable(ctx: FunParser.VariableContext): Int {
        val name = ctx.Identifier().text
        val value = ctx.expression()?.let { visit(ctx.expression()) } ?: 0
        scope.initializeVariable(name, value)
        return value
    }

    override fun visitIdentifierExpression(ctx: FunParser.IdentifierExpressionContext) =
            scope.getVariable(ctx.Identifier().text)

    override fun visitUnaryMinusExpression(ctx: FunParser.UnaryMinusExpressionContext) =
            -visit(ctx.expression())

    override fun visitExpressionWithParentheses(ctx: FunParser.ExpressionWithParenthesesContext): Int =
            visit(ctx.expression())

    override fun visitBinaryOperationExpression(ctx: FunParser.BinaryOperationExpressionContext): Int {
        val operation = ctx.op.text
        val lhs = visit(ctx.expression(0))

        if (operation == "||" && lhs != 0) {
            return 1
        }
        if (operation == "&&" && lhs == 0) {
            return 0
        }

        val rhs = visit(ctx.expression(1))

        fun Boolean.toInt() = if (this) 1 else 0

        return when(operation) {
            "+" -> lhs + rhs
            "-" -> lhs - rhs
            "*" -> lhs * rhs
            "/" -> if (rhs == 0) throw FunException("Division by zero!") else lhs / rhs
            "%" -> if (rhs == 0) throw FunException("Division by zero!") else lhs % rhs
            ">" -> (lhs > rhs).toInt()
            "<" -> (lhs < rhs).toInt()
            ">=" -> (lhs >= rhs).toInt()
            "<=" -> (lhs <= rhs).toInt()
            "==" -> (lhs == rhs).toInt()
            "!=" -> (lhs != rhs).toInt()
            "||", "&&" -> (rhs != 0).toInt()
            else -> throw FunException("Invalid operation!")
        }
    }

    override fun visitIdentifierFunctionCall(ctx: FunParser.IdentifierFunctionCallContext): Int {
        val function = scope.getFunction(ctx.Identifier().text)
        val arguments = ctx.arguments().expression().map { visit(it) }
        return function.invoke(arguments, printStream).let {
            returnValue = null
            it
        }
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext): Int {
        val name = ctx.Identifier().text
        val value = visit(ctx.expression())
        scope.assignVariable(name, value)
        return 0
    }

    override fun visitFunction(ctx: FunParser.FunctionContext): Int {
        val name = ctx.Identifier().text
        val parameterNames = ctx.parameterNames().Identifier().map { it.text }
        val body = ctx.blockWithBraces().block()
        scope.defineFunction(Function(name, parameterNames, body, scope.copy()))
        return 0
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Int {
        scope = Scope(scope)
        for (statement in ctx.statement()) {
            if (returnValue != null) {
                break
            }
            if (statement.returnStatement() != null) {
                returnValue = visit(statement.returnStatement())
                break
            }
            visit(statement)
        }
        scope = scope.parentScope!!
        return returnValue ?: 0
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext): Int =
            visit(ctx.block())

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext): Int {
        val condition = ctx.expression()
        while (visit(condition) != 0 && returnValue == null) {
            visit(ctx.blockWithBraces())
        }
        return returnValue ?: 0
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext): Int = when {
        visit(ctx.expression()) != 0 -> visit(ctx.blockWithBraces(0))
        ctx.blockWithBraces().size > 1 -> visit(ctx.blockWithBraces(1))
        else -> 0
    }
}