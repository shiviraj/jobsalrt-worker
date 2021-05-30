package com.jobsalrt.worker.test_utils

import org.reactivestreams.Publisher
import reactor.test.StepVerifier

fun <T> assertNextWith(publisher: Publisher<T>, assertions: (t: T) -> Unit) {
    StepVerifier.create(publisher)
        .consumeNextWith(assertions)
        .verifyComplete()
}

fun <T> expectNext(publisher: Publisher<T>, vararg response: T) {
    StepVerifier.create(publisher)
        .expectNext(*response)
        .verifyComplete()
}

fun <T> assertNextWith(publisher: Publisher<T>, first: (t: T) -> Unit, second: (t: T) -> Unit) {
    StepVerifier.create(publisher)
        .consumeNextWith(first)
        .consumeNextWith(second)
        .verifyComplete()
}

fun <T> assertNextWith(publisher: Publisher<T>, first: (t: T) -> Unit, second: (t: T) -> Unit, third: (t: T) -> Unit) {
    StepVerifier.create(publisher)
        .consumeNextWith(first)
        .consumeNextWith(second)
        .consumeNextWith(third)
        .verifyComplete()
}

fun <T> assertErrorWith(publisher: Publisher<T>, assertions: (t: Throwable) -> Unit) {
    StepVerifier.create(publisher)
        .consumeErrorWith(assertions)
        .verify()
}
