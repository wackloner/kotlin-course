package ru.spbau.mit

class Scope(
        val parentScope: Scope? = null
) {
    private val variables: MutableMap<String, Int> = HashMap()
    private val functions: MutableMap<String, Function> = HashMap()

    fun initializeVariable(name: String, value: Int = 0) {
        if (name in variables)
            throw FunException("Variable with such name already exists in current scope!")
        variables += name to value
    }

    fun assignVariable(name: String, value: Int) {
        if (name !in variables) {
            parentScope?.assignVariable(name, value) ?:
                    throw FunException("No such variable exists!")
        } else {
            variables[name] = value
        }
    }

    fun getVariable(name: String): Int = variables[name] ?:
                parentScope?.getVariable(name) ?: throw FunException("No such variable exists!")

    fun defineFunction(function: Function) {
        try {
            getFunction(function.name)
            throw FunException("Such function already exists!")
        } catch (exception: FunException) {
            functions += function.name to function
        }
    }

    fun getFunction(name: String): Function = functions[name] ?:
            parentScope?.getFunction(name) ?: throw FunException("No such function exists!")

    fun copy(): Scope {
        val copy = Scope(parentScope)
        variables.forEach { name, value -> copy.assignVariable(name, value) }
        functions.forEach { _, function -> copy.defineFunction(function) }
        return copy
    }

    fun debugShowVars() {
        print(variables)
        if (parentScope != null) {
            parentScope.debugShowVars()
        } else {
            println()
        }
    }
}