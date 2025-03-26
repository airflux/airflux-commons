// This file was automatically generated from StrictlyMappedList.kt by Knit tool. Do not edit.
package examples.test

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.knit.test.captureOutput

internal class StrictlyMappedList01Test : AnnotationSpec() {
    @Test
    fun testExampleStrictlyMappedList01() {
        val result = captureOutput("ExampleStrictlyMappedList01") { examples.exampleStrictlyMappedList01.main() }
        result shouldContainExactly listOf(
        )
    }
}
