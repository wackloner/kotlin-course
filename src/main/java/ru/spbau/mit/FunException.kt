package ru.spbau.mit

class FunException(
        override var message: String
) : Throwable(message)