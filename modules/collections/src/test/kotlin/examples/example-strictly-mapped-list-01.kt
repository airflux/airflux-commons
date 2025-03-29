// This file was automatically generated from StrictlyMappedList.kt by Knit tool. Do not edit.
package examples.exampleStrictlyMappedList01

import io.github.airflux.commons.collections.list.AbstractStrictlyMappedListElement
import io.github.airflux.commons.collections.list.StrictlyMappedList
import io.kotest.matchers.maps.shouldContain

@JvmInline
internal value class Details private constructor(
    val get: StrictlyMappedList = StrictlyMappedList.Empty
) {

    operator fun <E : StrictlyMappedList.Element> get(key: StrictlyMappedList.Key<E>): E? = get[key]

    fun toMap(): Map<String, String> =
        get.fold(mutableMapOf()) { acc, item ->
            acc.apply {
                val key = item.key.name
                if (key !in acc) this[key] = item.toString()
            }
        }

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

    class InconsistentData(column: Int, expectedType: String, actualType: String) : Errors {
        override val code: String = "Err-1"
        override val description: String = "Inconsistent data."
        override val details: Details =
            Details.from(
                Column(column),
                ExpectedType(expectedType),
                ActualType(actualType)
            )

        class Column(val value: Int) :
            AbstractStrictlyMappedListElement<Column>(Column) {

            override fun toString(): String = value.toString()

            companion object Key : StrictlyMappedList.Key<Column> {
                override val name: String = "column-index"
            }
        }

        class ExpectedType(val value: String) :
            AbstractStrictlyMappedListElement<ExpectedType>(ExpectedType) {

            override fun toString(): String = value

            companion object Key : StrictlyMappedList.Key<ExpectedType> {
                override val name: String = "expected-type"
            }
        }

        class ActualType(val value: String) :
            AbstractStrictlyMappedListElement<ActualType>(ActualType) {

            override fun toString(): String = value

            companion object Key : StrictlyMappedList.Key<ActualType> {
                override val name: String = "actual-type"
            }
        }
    }
}

internal fun main() {
    val error = Errors.InconsistentData(column = 1, expectedType = "String", actualType = "Int")
    val map = error.details.toMap()

    map shouldContain ("column-index" to "1")
    map shouldContain ("expected-type" to "String")
    map shouldContain ("actual-type" to "Int")
}
