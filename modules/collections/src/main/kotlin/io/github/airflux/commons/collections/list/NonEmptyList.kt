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

public typealias Nel<T> = NonEmptyList<T>

public fun <T> nonEmptyListOf(head: T, vararg tail: T): Nel<T> = NonEmptyList(head, tail.toList())

public fun <T> nonEmptyListOf(head: T, tail: List<T>): Nel<T> = NonEmptyList(head, tail.toList())

public fun <T> List<T>.nonEmptyListOrNull(): Nel<T>? = NonEmptyList.valueOf(this)

@JvmInline
public value class NonEmptyList<out T> private constructor(private val items: List<T>) : List<T> by items {

    public constructor(head: T, vararg tail: T) : this(head, tail.toList())

    public constructor(head: T, tail: List<T>) :
        this(
            buildList {
                add(head)
                addAll(tail)
            }
        )

    public operator fun plus(item: @UnsafeVariance T): NonEmptyList<T> = NonEmptyList(items + item)

    public operator fun plus(items: Iterable<@UnsafeVariance T>): NonEmptyList<T> = NonEmptyList(this.items + items)

    public operator fun plus(other: NonEmptyList<@UnsafeVariance T>): NonEmptyList<T> =
        NonEmptyList(this.items + other.items)

    public infix fun <R> map(transform: (T) -> R): NonEmptyList<R> = NonEmptyList(items.map(transform))

    public infix fun <R> flatMap(transform: (T) -> NonEmptyList<R>): NonEmptyList<R> =
        NonEmptyList(items.flatMap { transform(it).items })

    public companion object {

        @JvmStatic
        public fun <T> valueOf(list: List<T>): NonEmptyList<T>? =
            list.takeIf { it.isNotEmpty() }
                ?.let { NonEmptyList(it) }
    }
}
