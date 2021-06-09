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

    override fun toString(): String {
        return "DefaultTodo(text=$text, isDone=$isDone)"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is DefaultTodo) return false

        return other.text == text && other.isDone == isDone
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + isDone.hashCode()
        return result
    }
}
