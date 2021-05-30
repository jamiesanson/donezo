package dev.sanson.tick.screen.list

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import dev.sanson.tick.model.TodoList
import dev.sanson.tick.todo.Action
import dev.sanson.tick.todo.feature.database.DatabaseTodoList

// This class must be remember'd
class ListBloc(
    private val dispatch: (Any) -> Any
): Bloc<List<TodoList>, List<ListBloc.Row>> {

    data class InternalState(
        val focusNewItem: Boolean = false,
        val inOutMapping: Map<Any, Row> = emptyMap(),
    )

    override val state = derivedStateOf { computeState(internalState.value) }

    private val internalState = mutableStateOf(InternalState())

    private fun computeState(internalState: InternalState): List<Row> {

    }

    sealed interface Row {
        val id: String?
    }

    data class Title(
        override val id: String?,
        val title: String,
        val hasFocus: Boolean
    ): Row

    data class Item(
        override val id: String?,
        val text: String,
        val isDone: Boolean,
        val hasFocus: Boolean
    ): Row

    override fun onStateChange(newState: List<TodoList>) {
        currentExternalState = newState

    }

    fun updateTitle(row: Title, newTitle: String) = dispatch(Action.UpdateListTitle(
        list = listFor(row),
        title = newTitle
    ))

    fun addTodo(row: Title) {
        dispatch(Action.AddTodo(list = listFor(row)))
    }

    fun onFocusRequested(row: Row) {

    }

    fun onFocusCleared() {

    }

    private fun listFor(row: Title): TodoList {
        return currentExternalState.find { (it as? DatabaseTodoList)?.id?.toString() == row.id }!!
    }
}