package dev.sanson.tick.model

interface TodoList {
    val title: String
    val items: List<Todo>

    companion object {
        operator fun invoke(
            title: String,
            items: List<Todo>
        ): TodoList = object : TodoList {
            override val title: String = title
            override val items: List<Todo> = items
        }
    }
}

fun TodoList.copy(title: String) = object : TodoList by this {
    override val title: String = title
}

fun TodoList.copy(items: List<Todo>) = object: TodoList by this {
    override val items: List<Todo> = items
}