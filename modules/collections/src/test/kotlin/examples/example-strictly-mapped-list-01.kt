// This file was automatically generated from StrictlyMappedList.kt by Knit tool. Do not edit.
package examples.exampleStrictlyMappedList01

import io.github.airflux.commons.collections.list.AbstractStrictlyMappedListElement
import io.github.airflux.commons.collections.list.StrictlyMappedList

@JvmInline
internal value class Details private constructor(
    val get: StrictlyMappedList = StrictlyMappedList.Empty
) {

    operator fun <E : StrictlyMappedList.Element> get(key: StrictlyMappedList.Key<E>): E? = get[key]

    companion object {

        operator fun invoke(): Details = Details()

        fun from(vararg elements: StrictlyMappedList.Element): Details =
            Details(StrictlyMappedList.from(elements.asIterable()))
    }
}


internal sealed interface Errors {
    val code: String
    val description: String
    val details: Details get() = Details()

    class InconsistentData(column: Int, exception: Exception) : Errors {
        override val code: String = "Err-1"
        override val description: String = "Inconsistent data."
        override val details: Details =
            Details.from(
                Column(column),
                ExceptionMessage(exception),
                StackTrace(exception)
            )
    }

    class Column(val get: Int) : AbstractStrictlyMappedListElement<Column>(Column) {

        override fun toString(): String = get.toString()

        companion object Key : StrictlyMappedList.Key<Column> {
            override val name: String = "column-index"
        }
    }

    class ExceptionMessage(val get: Exception) :
        AbstractStrictlyMappedListElement<ExceptionMessage>(ExceptionMessage) {

        override fun toString(): String = get.message ?: "No message."

        companion object Key : StrictlyMappedList.Key<ExceptionMessage> {
            override val name: String = "exception-message"
        }
    }

    class StackTrace(val get: Exception) : AbstractStrictlyMappedListElement<StackTrace>(StackTrace) {

        override fun toString(): String = get.stackTraceToString()

        companion object Key : StrictlyMappedList.Key<StackTrace> {
            override val name: String = "stack-trace"
        }
    }
}

