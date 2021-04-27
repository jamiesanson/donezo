package dev.sanson.tick.model

interface Todo {
    val text: String
    val isDone: Boolean

    companion object {
        operator fun invoke(
            text: String,
            isDone: Boolean
        ): Todo = object: Todo {
            override val text: String = text
            override val isDone: Boolean = isDone
        }
    }
}

fun Todo.copy(text: String) = object: Todo by this {
    override val text: String = text
}

fun Todo.copy(isDone: Boolean) = object: Todo by this {
    override val isDone: Boolean = isDone
}
