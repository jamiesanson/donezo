package dev.sanson.tick.model

interface TodoList {
    val title: String
    val items: List<Todo>

    fun copy(title: String = this.title, items: List<Todo> = this.items): TodoList

    companion object {
        operator fun invoke(
            title: String,
            items: List<Todo>
        ): TodoList = DefaultTodoList(title, items)
    }
}

class DefaultTodoList(
    override val title: String,
    override val items: List<Todo>
) : TodoList {

    override fun copy(title: String, items: List<Todo>): TodoList {
        return DefaultTodoList(title, items)
    }
}
