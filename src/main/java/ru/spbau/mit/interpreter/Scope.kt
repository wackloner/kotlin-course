package ru.spbau.mit.interpreter

class Scope(
        var parentScope: Scope? = null
) {
    private var variables: MutableMap<String, Int> = HashMap()
    private var functions: MutableMap<String, Function> = HashMap()

    fun initializeVariable(name: String, value: Int = 0) {
        FunException.assert(
                name in variables,
            "Variable with such name already exists in current scope!"
        )
        variables.put(name, value)
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
        FunException.assert(
                getFunctionOrNull(function.name) != null,
                "Such function already exists!"
        )
        functions.put(function.name, function)
    }

    private fun getFunctionOrNull(name: String): Function? = functions[name] ?:
            parentScope?.getFunctionOrNull(name)

    fun getFunction(name: String): Function = getFunctionOrNull(name) ?:
            throw FunException("No such function exists!")

    fun copy(): Scope {
        val copy = Scope(parentScope?.copy())
        copy.variables = HashMap(variables)
        copy.functions = HashMap(functions)
        return copy
    }

    fun clear() {
        parentScope = null
        variables.clear()
        functions.clear()
    }

    fun debugShowVars() {
        print(variables)
        parentScope?.debugShowVars() ?: println()
    }
}