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

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public fun <T> nonEmptyListOf(head: T, vararg tail: T): NonEmptyList<T> = nonEmptyListOf(head, tail.toList())

public fun <T> nonEmptyListOf(head: T, tail: List<T>): NonEmptyList<T> = NonEmptyList.valueOf(head, tail)

public fun <T> List<T>.toNonEmptyListOrNull(): NonEmptyList<T>? = NonEmptyList.valueOf(this)

@JvmInline
public value class NonEmptyList<out T> private constructor(private val items: List<T>) : List<T> by items {

    public companion object {

        @JvmStatic
        public fun <T> valueOf(head: T, tail: List<T>): NonEmptyList<T> = NonEmptyList(merge(head, tail))

        @JvmStatic
        public fun <T> valueOf(list: List<T>): NonEmptyList<T>? =
            list.takeIf { it.isNotEmpty() }
                ?.let { NonEmptyList(it) }

        @JvmStatic
        public fun <T> plus(origin: NonEmptyList<T>, item: T): NonEmptyList<T> = NonEmptyList(origin.items + item)

        @JvmStatic
        public fun <T> plus(origin: NonEmptyList<T>, items: Iterable<T>): NonEmptyList<T> =
            NonEmptyList(origin.items + items)

        @JvmStatic
        public fun <T> plus(origin: NonEmptyList<T>, other: NonEmptyList<T>): NonEmptyList<T> =
            NonEmptyList(origin.items + other.items)

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <T, R> map(origin: NonEmptyList<T>, transform: (T) -> R): NonEmptyList<R> {
            contract {
                callsInPlace(transform, InvocationKind.AT_LEAST_ONCE)
            }
            return (origin as List<T>)
                .map(transform)
                .toNonEmptyListOrNull()!!
        }

        @OptIn(ExperimentalContracts::class)
        @JvmStatic
        public inline fun <T, R> flatMap(origin: NonEmptyList<T>, transform: (T) -> NonEmptyList<R>): NonEmptyList<R> {
            contract {
                callsInPlace(transform, InvocationKind.AT_LEAST_ONCE)
            }
            return (origin as List<T>)
                .flatMap { transform(it) }
                .toNonEmptyListOrNull()!!
        }

        @JvmStatic
        private fun <T> merge(head: T, tail: List<T>) = buildList {
            add(head)
            addAll(tail)
        }
    }
}

public operator fun <T> NonEmptyList<T>.plus(item: T): NonEmptyList<T> = NonEmptyList.plus(this, item)
public operator fun <T> NonEmptyList<T>.plus(items: Iterable<T>): NonEmptyList<T> = NonEmptyList.plus(this, items)
public operator fun <T> NonEmptyList<T>.plus(other: NonEmptyList<T>): NonEmptyList<T> = NonEmptyList.plus(this, other)

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> NonEmptyList<T>.map(transform: (T) -> R): NonEmptyList<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_LEAST_ONCE)
    }
    return NonEmptyList.map(this, transform)
}

@OptIn(ExperimentalContracts::class)
public inline fun <T, R> NonEmptyList<T>.flatMap(transform: (T) -> NonEmptyList<R>): NonEmptyList<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_LEAST_ONCE)
    }
    return NonEmptyList.flatMap(this, transform)
}
