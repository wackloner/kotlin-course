package ru.spbau.mit

class InterpretationVisitor(
        private var scope: Scope = Scope()
) : FunBaseVisitor<Int>() {
    companion object {
        val returnValue: ReturnValue = ReturnValue()
    }

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext?): Int {
        return ctx!!.text.toInt()
    }

    override fun visitPrintlnFunctionCall(ctx: FunParser.PrintlnFunctionCallContext?): Int {
        val arguments = ctx!!.arguments().expression()
        println(arguments.map { visit(it) }.joinToString(" "))
        return 0
    }

    override fun visitVariable(ctx: FunParser.VariableContext?): Int {
        val name = ctx!!.Identifier().text
        val value = if (ctx.expression() != null) visit(ctx.expression()) else 0
        scope.initializeVariable(name, value)
        return value
    }

    override fun visitIdentifierExpression(ctx: FunParser.IdentifierExpressionContext?): Int {
        return scope.getVariable(ctx!!.Identifier().text)
    }

    override fun visitUnaryMinusExpression(ctx: FunParser.UnaryMinusExpressionContext?): Int {
        return -visit(ctx!!.expression())
    }

    override fun visitExpressionWithParentheses(ctx: FunParser.ExpressionWithParenthesesContext?): Int {
        return visit(ctx!!.expression())
    }

    override fun visitBinaryOperationExpression(ctx: FunParser.BinaryOperationExpressionContext?): Int {
        val operation = ctx!!.op.text
        val lhs = visit(ctx.expression(0))

        if (operation == "||" && lhs != 0)
            return 1
        if (operation == "&&" && lhs == 0)
            return 0

        val rhs = visit(ctx.expression(1))

        fun toInt(x: Boolean) = if (x) 1 else 0

        return when(operation) {
            "+" -> lhs + rhs
            "-" -> lhs - rhs
            "*" -> lhs * rhs
            "/" -> if (rhs == 0) throw FunException("Division by zero!") else lhs / rhs
            "%" -> if (rhs == 0) throw FunException("Division by zero!") else lhs % rhs
            ">" -> toInt(lhs > rhs)
            "<" -> toInt(lhs < rhs)
            ">=" -> toInt(lhs >= rhs)
            "<=" -> toInt(lhs <= rhs)
            "==" -> toInt(lhs == rhs)
            "!=" -> toInt(lhs != rhs)
            "||", "&&" -> toInt(rhs != 0)
            else -> throw FunException("Invalid operation!")
        }
    }

    override fun visitIdentifierFunctionCall(ctx: FunParser.IdentifierFunctionCallContext?): Int {
        val function = scope.getFunction(ctx!!.Identifier().text)
        val arguments = ctx.arguments().expression().map { visit(it) }
        return function.invoke(arguments)
    }

    override fun visitAssignment(ctx: FunParser.AssignmentContext?): Int {
        val name = ctx!!.Identifier().text
        val value = visit(ctx.expression())
        scope.assignVariable(name, value)
        return 0
    }

    override fun visitFunction(ctx: FunParser.FunctionContext?): Int {
        val name = ctx!!.Identifier().text
        val parameterNames = ctx.parameterNames().Identifier().map { it.text }
        val body = ctx.blockWithBraces().block()
        scope.defineFunction(Function(name, parameterNames, body, scope.copy()))
        return 0
    }

    override fun visitBlock(ctx: FunParser.BlockContext?): Int {
        scope = Scope(scope)
        var returned = false
        for (statement in ctx!!.statement()) {
            if (statement.returnStatement() != null) {
                returnValue.value = visit(statement.returnStatement())
                returned = true
                break
            }
            visit(statement)
        }
        scope = scope.parentScope!!
        if (returned)
            throw returnValue
        return 0
    }

    override fun visitBlockWithBraces(ctx: FunParser.BlockWithBracesContext?): Int {
        return visit(ctx!!.block())
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext?): Int {
        val condition = ctx!!.expression()
        while (visit(condition) != 0)
            visit(ctx.blockWithBraces())
        return 0
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext?): Int = when {
        visit(ctx!!.expression()) != 0 -> visit(ctx.blockWithBraces(0))
        ctx.blockWithBraces().size > 1 -> visit(ctx.blockWithBraces(1))
        else -> 0
    }
}