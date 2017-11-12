package ru.spbau.mit.ast

import ru.spbau.mit.ast.FunAST.*

object ExampleFunAST {
    val FIRST = FunAST(
            Block(
                    listOf(
                            Variable("a", Literal(3)),
                            Variable("b", Literal(5)),
                            If(
                                    BinaryOperation(
                                            Identifier("b"),
                                            ">",
                                            Identifier("a")
                                    ),
                                    BracedBlock(Block(listOf(
                                            PrintFunctionCall(
                                                    Arguments(listOf(Identifier("b")))
                                            )
                                    ))),
                                    BracedBlock(Block(listOf(
                                            PrintFunctionCall(
                                                    Arguments(listOf(
                                                            BinaryOperation(
                                                                    Identifier("a"),
                                                                    "-",
                                                                    Identifier("b")
                                                            )
                                                    ))
                                            )
                                    )))
                            )
                    )
            )
    )

    val SECOND = FunAST(
            Block(
                    listOf(
                            FunctionDeclaration(
                                    "fib",
                                    ParameterNames(listOf("n")),
                                    BracedBlock(Block(listOf(
                                            If(
                                                    BinaryOperation(
                                                            Identifier("n"),
                                                            "<=",
                                                            Literal(1)
                                                    ),
                                                    BracedBlock(Block(listOf(
                                                            Return(Literal(1))
                                                    )))
                                            ),
                                            Return(BinaryOperation(
                                                    IdentifierFunctionCall(
                                                            "fib",
                                                            Arguments(listOf(BinaryOperation(
                                                                    Identifier("n"),
                                                                    "-",
                                                                    Literal(1)
                                                            )))
                                                    ),
                                                    "+",
                                                    IdentifierFunctionCall(
                                                            "fib",
                                                            Arguments(listOf(BinaryOperation(
                                                                    Identifier("n"),
                                                                    "-",
                                                                    Literal(2)
                                                            )))
                                                    )
                                            ))
                                    )))
                            ),
                            Variable(
                                    "i",
                                    Literal(1)
                            ),
                            While(
                                    BinaryOperation(
                                            Identifier("i"),
                                            "<=",
                                            Literal(5)
                                    ),
                                    BracedBlock(Block(listOf(
                                            PrintFunctionCall(Arguments(listOf(
                                                    Identifier("i"),
                                                    IdentifierFunctionCall(
                                                            "fib",
                                                            Arguments(listOf(Identifier("i")))
                                                    )
                                            ))),
                                            Assignment(
                                                    "i",
                                                    BinaryOperation(
                                                            Identifier("i"),
                                                            "+",
                                                            Literal(1)
                                                    )
                                            )
                                    )))
                            )
                    )
            )
    )
}