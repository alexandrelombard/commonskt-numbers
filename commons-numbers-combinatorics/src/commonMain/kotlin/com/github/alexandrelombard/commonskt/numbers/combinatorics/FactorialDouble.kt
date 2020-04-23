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
 * Class for computing the natural logarithm of the
 * [factorial of a number](http://mathworld.wolfram.com/Factorial.html).
 * It allows to allocate a cache of precomputed values.
 */
class FactorialDouble
    /**
     * Creates an instance, reusing the already computed values if available.
     *
     * @param numValues Number of values of the function to compute.
     * @param cache Cached values.
     * @throw IllegalArgumentException if `n < 0`.
     */
    private constructor(
        numValues: Int,
        cache: DoubleArray?) {
    /**
     * Precomputed values of the function: `factorialsDouble[i] = i!`.
     */
    private val factorialsDouble: DoubleArray

    /**
     * Creates an instance with the specified cache size.
     *
     * @param cacheSize Number of precomputed values of the function.
     * @return a new instance where `cacheSize` values have been
     * precomputed.
     * @throws IllegalArgumentException if `cacheSize < 0`.
     */
    fun withCache(cacheSize: Int): FactorialDouble {
        return FactorialDouble(
            cacheSize,
            factorialsDouble
        )
    }

    /**
     * Computes the factorial of `n`.
     * The result should be small enough to fit into a `double`: The
     * largest `n` for which `n!` does not exceed
     * `Double.MAX_VALUE` is 170. `Double.POSITIVE_INFINITY` is
     * returned for `n > 170`.
     *
     * @param n Argument.
     * @return `n!`
     * @throws IllegalArgumentException if `n < 0`.
     */
    fun value(n: Int): Double {
        if (n < FACTORIALS_LONG_CACHE_SIZE) {
            return Factorial.value(n).toDouble()
        }
        return if (n < factorialsDouble.size) {
            // Use cache of precomputed values.
            factorialsDouble[n]
        } else compute(n)
    }

    /**
     * @param n Argument.
     * @return `n!` (approximated as a `double`).
     */
    private fun compute(n: Int): Double {
        var start = 2
        var result = 1.0
        if (factorialsDouble.size > 2) {
            result = factorialsDouble[factorialsDouble.size - 1]
            start = factorialsDouble.size
        }
        for (i in start..n) {
            result *= i.toDouble()
        }
        return result
    }

    companion object {
        /**
         * Size of precomputed factorials.
         * @see Factorial
         */
        private const val FACTORIALS_LONG_CACHE_SIZE = 21

        /**
         * Creates an instance with no precomputed values.
         * @return instance with no precomputed values
         */
        fun create(): FactorialDouble {
            return FactorialDouble(
                0,
                null
            )
        }
    }

    init {
        if (numValues < 0) {
            throw CombinatoricsException(
                CombinatoricsException.NEGATIVE(
                    numValues
                )
            )
        }
        factorialsDouble = DoubleArray(numValues)
        // Initialize first two entries.
        val max = if (numValues < 2) numValues else 2
        for (i in 0 until max) {
            factorialsDouble[i] = 1.0
        }
        val beginCopy = 2
        val endCopy =
            if (cache == null || cache.size <= beginCopy) beginCopy else if (cache.size <= numValues) cache.size else numValues

        // Copy available values.
        for (i in beginCopy until endCopy) {
            factorialsDouble[i] = cache!![i]
        }

        // Precompute.
        for (i in endCopy until numValues) {
            factorialsDouble[i] = i * factorialsDouble[i - 1]
        }
    }
}
