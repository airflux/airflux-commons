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
import io.github.airflux.commons.text.CaseInsensitiveString
import io.github.airflux.commons.text.caseInsensitive

public abstract class AbstractCaseInsensitiveMutableMap<T>(
    factory: () -> MutableMap<CaseInsensitiveString, T>
) : MutableMap<String, T> {

    protected val target: MutableMap<CaseInsensitiveString, T> = factory()

    override val size: Int
        get() = target.size

    override fun isEmpty(): Boolean = target.isEmpty()

    override fun containsKey(key: String): Boolean = target.containsKey(CaseInsensitiveString(key))

    override fun containsValue(value: T): Boolean = target.containsValue(value)

    override fun get(key: String): T? = target[key.caseInsensitive()]

    override fun put(key: String, value: T): T? = target.put(key.caseInsensitive(), value)

    override fun putAll(from: Map<out String, T>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    override fun remove(key: String): T? = target.remove(key.caseInsensitive())

    override fun clear() {
        target.clear()
    }

    override val keys: MutableSet<String>
        get() = MutableSetProxy(
            target = target.keys,
            convertToExternal = { get },
            convertToInternal = { caseInsensitive() }
        )

    override val entries: MutableSet<MutableMap.MutableEntry<String, T>>
        get() = MutableSetProxy(
            target = target.entries,
            convertToExternal = { Entry(key.get, value) },
            convertToInternal = { Entry(key.caseInsensitive(), value) }
        )

    override val values: MutableCollection<T> get() = target.values

    internal class Entry<K : Any, T>(
        override val key: K,
        override var value: T
    ) : MutableMap.MutableEntry<K, T> {

        override fun setValue(newValue: T): T {
            val previous = value
            value = newValue
            return previous
        }

        override fun hashCode(): Int = key.hashCode()

        override fun equals(other: Any?): Boolean = when {
            this === other -> true
            other is Map.Entry<*, *> -> key == other.key
            else -> false
        }

        override fun toString(): String = "$key=$value"
    }

    internal class MutableSetProxy<INTERNAL, EXTERNAL>(
        private val target: MutableSet<INTERNAL>,
        private val convertToExternal: INTERNAL.() -> EXTERNAL,
        private val convertToInternal: EXTERNAL.() -> INTERNAL
    ) : MutableSet<EXTERNAL> {

        override val size: Int
            get() = target.size

        override fun isEmpty(): Boolean = target.isEmpty()

        override fun iterator(): MutableIterator<EXTERNAL> = object : MutableIterator<EXTERNAL> {
            private val iterator = target.iterator()

            override fun hasNext(): Boolean = iterator.hasNext()

            override fun next(): EXTERNAL = iterator.next().convertToExternal()

            override fun remove() = iterator.remove()
        }

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

        override fun hashCode(): Int = target.hashCode()

        override fun equals(other: Any?): Boolean =
            if (this === other)
                EQUAL
            else if (other is Set<*>) {
                val items = target.convertToExternal()
                items.containsAll(other) && other.containsAll(items)
            } else
                NOT_EQUAL

        override fun toString(): String = target.convertToExternal().toString()

        private companion object {
            private const val EQUAL = true
            private const val NOT_EQUAL = false
        }

        private fun Collection<EXTERNAL>.convertToInternal(): Set<INTERNAL> = mapToSet { it.convertToInternal() }
        private fun Collection<INTERNAL>.convertToExternal(): Set<EXTERNAL> = mapToSet { it.convertToExternal() }
    }
}
