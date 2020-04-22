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

package org.apache.commonskt.numbers.combinatorics

import org.apache.commons.numbers.gamma.LogGamma
import kotlin.math.ln

/**
 * Class for computing the natural logarithm of the factorial of a number.
 * It allows to allocate a cache of precomputed values.
 * In case of cache miss, computation is performed by a call to
 * [LogGamma.value].
 */
class LogFactorial private constructor(
    numValues: Int,
    cache: DoubleArray?
) {
    /**
     * Precomputed values of the function: `logFactorials[i] = Math.log(i!)`.
     */
    private val logFactorials: DoubleArray

    /**
     * Creates an instance with the specified cache size.
     *
     * @param cacheSize Number of precomputed values of the function.
     * @return a new instance where `cacheSize` values have been
     * precomputed.
     * @throws IllegalArgumentException if `cacheSize < 0`.
     */
    fun withCache(cacheSize: Int): LogFactorial {
        return LogFactorial(cacheSize, logFactorials)
    }

    /**
     * Computes \( log_e(n!) \).
     *
     * @param n Argument.
     * @return `log(n!)`.
     * @throws IllegalArgumentException if `n < 0`.
     */
    fun value(n: Int): Double {
        if (n < 0) {
            throw CombinatoricsException(CombinatoricsException.NEGATIVE(n))
        }

        // Use cache of precomputed values.
        if (n < logFactorials.size) {
            return logFactorials[n]
        }

        // Use cache of precomputed factorial values.
        return if (n < FACTORIALS_CACHE_SIZE) {
            ln(Factorial.value(n).toDouble())
        } else LogGamma.value(n + 1.0)

        // Delegate.
    }

    companion object {
        /**
         * Size of precomputed factorials.
         * @see Factorial
         */
        private const val FACTORIALS_CACHE_SIZE = 21

        /**
         * Creates an instance with no precomputed values.
         * @return instance with no precomputed values
         */
        fun create(): LogFactorial {
            return LogFactorial(0, null)
        }
    }

    /**
     * Creates an instance, reusing the already computed values if available.
     *
     * @param numValues Number of values of the function to compute.
     * @param cache Cached values.
     * @throw IllegalArgumentException if `n < 0`.
     */
    init {
        if (numValues < 0) {
            throw CombinatoricsException(CombinatoricsException.NEGATIVE(numValues))
        }
        logFactorials = DoubleArray(numValues)
        val beginCopy = 2
        val endCopy =
            if (cache == null || cache.size <= beginCopy) beginCopy else if (cache.size <= numValues) cache.size else numValues

        // Copy available values.
        for (i in beginCopy until endCopy) {
            logFactorials[i] = cache!![i]
        }

        // Precompute.
        for (i in endCopy until numValues) {
            logFactorials[i] = logFactorials[i - 1] + ln(i.toDouble())
        }
    }
}
