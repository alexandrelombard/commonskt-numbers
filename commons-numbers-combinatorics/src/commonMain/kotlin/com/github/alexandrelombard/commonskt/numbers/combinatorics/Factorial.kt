/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexandrelombard.commonskt.numbers.combinatorics

/**
 * [Factorial of a number](http://mathworld.wolfram.com/Factorial.html).
 */
object Factorial {
    /** All long-representable factorials.  */
    val FACTORIALS = longArrayOf(
        1L, 1L, 2L,
        6L, 24L, 120L,
        720L, 5040L, 40320L,
        362880L, 3628800L, 39916800L,
        479001600L, 6227020800L, 87178291200L,
        1307674368000L, 20922789888000L, 355687428096000L,
        6402373705728000L, 121645100408832000L, 2432902008176640000L
    )

    /**
     * Computes the factorial of `n`.
     *
     * @param n Argument.
     * @return `n!`
     * @throws IllegalArgumentException if `n < 0`.
     * @throws IllegalArgumentException if `n > 20` (the factorial
     * value is too large to fit in a `long`).
     */
    fun value(n: Int): Long {
        if (n < 0 ||
            n > 20
        ) {
            throw CombinatoricsException(
                CombinatoricsException.OUT_OF_RANGE(
                    n,
                    0,
                    20
                )
            )
        }
        return FACTORIALS[n]
    }
}
