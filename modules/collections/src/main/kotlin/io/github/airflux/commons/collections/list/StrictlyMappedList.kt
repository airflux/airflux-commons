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

public sealed interface StrictlyMappedList {

    public val isEmpty: Boolean

    public operator fun <ElementT : Element> get(key: Key<ElementT>): ElementT?

    public operator fun <ElementT : Element> contains(key: Key<ElementT>): Boolean

    public operator fun <ElementT : Element> plus(element: ElementT): StrictlyMappedList =
        Combined(head = element, tail = this)

    public fun <ResultT> fold(initial: ResultT, operation: (ResultT, Element) -> ResultT): ResultT

    public interface Key<ElementT : Element>

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

    private companion object {
        private const val CONTAINS = true
        private const val NOT_CONTAINS = false
    }
}

public val StrictlyMappedList.isNotEmpty: Boolean
    get() = !isEmpty
