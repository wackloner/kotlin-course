package ru.spbau.mit.interpreter

class FunException(
        override var message: String
) : Throwable(message)