package ru.spbau.mit.interpreter

class FunException(
        override var message: String
) : Throwable(message) {
    companion object {
        fun assert(condition: Boolean, errorMessage: String) {
            if (condition) {
                throw FunException(errorMessage)
            }
        }
    }
}