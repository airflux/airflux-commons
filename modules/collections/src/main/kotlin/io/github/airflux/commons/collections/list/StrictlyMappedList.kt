/*
 * Copyright 2024 Maxim Sambulat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.airflux.commons.collections.list

/**
 *
 * Represents a data structure for managing elements that are associated with specific keys.
 * The list is strictly mapped, ensuring well-defined operations and a type-safe interaction
 * between keys and their corresponding elements.
 *
 * Example:
 * <!--- INCLUDE
 * import io.github.airflux.commons.collections.list.AbstractStrictlyMappedListElement
 * import io.github.airflux.commons.collections.list.StrictlyMappedList
 * -->
 * ```kotlin
 * @JvmInline
 * internal value class Details private constructor(
 *     val get: StrictlyMappedList = StrictlyMappedList.Empty
 * ) {
 *
 *     operator fun <E : StrictlyMappedList.Element> get(key: StrictlyMappedList.Key<E>): E? = get[key]
 *
 *     companion object {
 *
 *         operator fun invoke(): Details = Details()
 *
 *         fun from(vararg elements: StrictlyMappedList.Element): Details =
 *             Details(StrictlyMappedList.from(elements.asIterable()))
 *     }
 * }
 *
 *
 * internal sealed interface Errors {
 *     val code: String
 *     val description: String
 *     val details: Details get() = Details()
 *
 *     class InconsistentData(column: Int, exception: Exception) : Errors {
 *         override val code: String = "Err-1"
 *         override val description: String = "Inconsistent data."
 *         override val details: Details =
 *             Details.from(
 *                 Column(column),
 *                 ExceptionMessage(exception),
 *                 StackTrace(exception)
 *             )
 *     }
 *
 *     class Column(val get: Int) : AbstractStrictlyMappedListElement<Column>(Column) {
 *
 *         override fun toString(): String = get.toString()
 *
 *         companion object Key : StrictlyMappedList.Key<Column> {
 *             override val name: String = "column-index"
 *         }
 *     }
 *
 *     class ExceptionMessage(val get: Exception) : AbstractStrictlyMappedListElement<Column>(Column) {
 *
 *         override fun toString(): String = get.message ?: "No message."
 *
 *         companion object Key : StrictlyMappedList.Key<Column> {
 *             override val name: String = "exception-message"
 *         }
 *     }
 *
 *     class StackTrace(val get: Exception) : AbstractStrictlyMappedListElement<Column>(Column) {
 *
 *         override fun toString(): String = get.stackTraceToString()
 *
 *         companion object Key : StrictlyMappedList.Key<Column> {
 *             override val name: String = "stack-trace"
 *         }
 *     }
 * }
 *
 * ```
 * <!--- KNIT example-strictly-mapped-list-01.kt -->
 */
public sealed interface StrictlyMappedList {

    /**
     * Indicates whether the list is empty.
     *
     * This property is true if the list contains no elements, otherwise false.
     */
    public val isEmpty: Boolean

    /**
     * Retrieves the last element from the mapped list that matches the specified key.
     *
     * @param key the key associated with the desired element
     * @return the element associated with the specified key, or null if no such element exists
     */
    public operator fun <ElementT : Element> get(key: Key<ElementT>): ElementT?

    /**
     * Checks whether the given key exists within the list.
     *
     * @param key The key associated with the element to search for in the list.
     * @return True if the element corresponding to the given key exists in the list, false otherwise.
     */
    public operator fun <ElementT : Element> contains(key: Key<ElementT>): Boolean

    /**
     * Combines the current [StrictlyMappedList] with the given element, returning a new instance
     * containing the element as the head and the current list as the tail.
     *
     * @param element The element to be added to the list.
     * @return A new [StrictlyMappedList] that includes the specified element.
     */
    public operator fun <ElementT : Element> plus(element: ElementT): StrictlyMappedList =
        Combined(head = element, tail = this)

    /**
     * Applies a folding operation over the elements of the list, starting with the given [initial] value.
     *
     * @param initial The initial value to start the folding operation.
     * @param operation A function that combines the accumulated value and the current element.
     * @return The result of applying the folding operation to all elements in the list.
     */
    public fun <ResultT> fold(initial: ResultT, operation: (ResultT, Element) -> ResultT): ResultT

    public interface Key<ElementT : Element> {

        /**
         * Provides a string identifier for the key.
         */
        public val name: String
    }

    public interface Element : StrictlyMappedList {
        public val key: Key<*>

        override val isEmpty: Boolean
            get() = false

        override fun <ElementT : Element> get(key: Key<ElementT>): ElementT? =
            if (this.key == key)
                @Suppress("UNCHECKED_CAST")
                requireNotNull(this as? ElementT)
            else
                null

        override fun <ElementT : Element> contains(key: Key<ElementT>): Boolean = this.key == key

        public override fun <ResultT> fold(initial: ResultT, operation: (ResultT, Element) -> ResultT): ResultT =
            operation(initial, this)
    }

    public data object Empty : StrictlyMappedList {
        override val isEmpty: Boolean = true
        override fun <ElementT : Element> get(key: Key<ElementT>): ElementT? = null
        override fun <ElementT : Element> contains(key: Key<ElementT>): Boolean = NOT_CONTAINS
        override fun <ResultT> fold(initial: ResultT, operation: (ResultT, Element) -> ResultT): ResultT = initial
    }

    private class Combined(val head: Element, val tail: StrictlyMappedList) : StrictlyMappedList {

        override val isEmpty: Boolean = false

        override fun <ElementT : Element> get(key: Key<ElementT>): ElementT? = get(key, this)

        override fun <ElementT : Element> contains(key: Key<ElementT>): Boolean = contains(key, this)

        override fun <ResultT> fold(initial: ResultT, operation: (ResultT, Element) -> ResultT): ResultT =
            fold(initial, this, operation)

        private tailrec fun <ElementT : Element> get(key: Key<ElementT>, element: StrictlyMappedList): ElementT? =
            when (element) {
                is Combined -> {
                    val value = element.head[key]
                    if (value != null) value else get(key, element.tail)
                }

                is Empty,
                is Element -> element[key]
            }

        private tailrec fun <ElementT : Element> contains(key: Key<ElementT>, element: StrictlyMappedList): Boolean =
            when (element) {
                is Empty -> key in element
                is Element -> key in element
                is Combined -> if (key in element.head) CONTAINS else contains(key, element.tail)
            }

        private tailrec fun <ResultT> fold(
            initial: ResultT,
            element: StrictlyMappedList,
            operation: (ResultT, Element) -> ResultT
        ): ResultT =
            when (element) {
                is Empty -> initial
                is Element -> operation(initial, element)
                is Combined -> fold(operation(initial, element.head), element.tail, operation)
            }
    }

    public companion object {

        public fun from(vararg elements: Element): StrictlyMappedList =
            from(elements.asIterable())

        public fun from(elements: Iterable<Element>): StrictlyMappedList =
            elements.fold(Empty as StrictlyMappedList) { acc, item ->
                acc + item
            }

        private const val CONTAINS = true
        private const val NOT_CONTAINS = false
    }
}

/**
 * Indicates whether the list is not empty.
 *
 * This property is true if the list contains one or more elements, otherwise false.
 */
public val StrictlyMappedList.isNotEmpty: Boolean
    get() = !isEmpty
