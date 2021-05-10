package dev.sanson.tick.model

interface Todo {
    val text: String
    val isDone: Boolean

    fun copy(text: String = this.text, isDone: Boolean = this.isDone): Todo

    companion object {
        operator fun invoke(text: String, isDone: Boolean) = DefaultTodo(text, isDone)
    }
}

class DefaultTodo(
    override val text: String,
    override val isDone: Boolean
): Todo {
    override fun copy(text: String, isDone: Boolean): Todo {
        return DefaultTodo(text, isDone)
    }
}
