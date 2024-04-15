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

package io.github.airflux.commons.collections.map

import io.github.airflux.commons.collections.extensions.mapToSet

internal class MutableSetProxy<INTERNAL : Any, EXTERNAL : Any>(
    private val target: MutableSet<INTERNAL>,
    private val convertToExternal: INTERNAL.() -> EXTERNAL,
    private val convertToInternal: EXTERNAL.() -> INTERNAL
) : MutableSet<EXTERNAL> {

    override val size: Int
        get() = target.size

    override fun isEmpty(): Boolean = target.isEmpty()

    override fun iterator(): MutableIterator<EXTERNAL> = MutableIteratorProxy(target, convertToExternal)

    override fun contains(element: EXTERNAL): Boolean = target.contains(element.convertToInternal())

    override fun containsAll(elements: Collection<EXTERNAL>): Boolean =
        target.containsAll(elements.convertToInternal())

    override fun add(element: EXTERNAL): Boolean = target.add(element.convertToInternal())

    override fun addAll(elements: Collection<EXTERNAL>): Boolean = target.addAll(elements.convertToInternal())

    override fun remove(element: EXTERNAL): Boolean = target.remove(element.convertToInternal())

    override fun removeAll(elements: Collection<EXTERNAL>): Boolean = target.removeAll(elements.convertToInternal())

    override fun retainAll(elements: Collection<EXTERNAL>): Boolean = target.retainAll(elements.convertToInternal())

    override fun clear() {
        target.clear()
    }

    override fun hashCode(): Int = target.convertToExternal().hashCode()

    override fun equals(other: Any?): Boolean = if (this === other)
        EQUAL
    else if (other is MutableSetProxy<*, *>)
        target.match(other)
    else if (other is Set<*>)
        target.match(other)
    else
        NOT_EQUAL

    override fun toString(): String = target.convertToExternal().toString()

    private fun Collection<EXTERNAL>.convertToInternal(): Set<INTERNAL> = mapToSet { it.convertToInternal() }
    private fun Collection<INTERNAL>.convertToExternal(): Set<EXTERNAL> = mapToSet { it.convertToExternal() }
    private fun Set<INTERNAL>.match(other: Set<*>): Boolean {
        if (this.size != other.size) return NOT_EQUAL
        for (item in this) {
            if (item.convertToExternal() !in other) return NOT_EQUAL
        }
        return EQUAL
    }

    private companion object {
        private const val EQUAL = true
        private const val NOT_EQUAL = false
    }
}
