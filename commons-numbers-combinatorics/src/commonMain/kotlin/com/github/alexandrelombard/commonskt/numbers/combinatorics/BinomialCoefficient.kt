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

import com.github.alexandrelombard.commonskt.math.multiplyExact
import com.github.alexandrelombard.commonskt.numbers.core.ArithmeticUtils

/**
 * Representation of the [
 * binomial coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html).
 * It is "`n choose k`", the number of `k`-element subsets that
 * can be selected from an `n`-element set.
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object BinomialCoefficient {
    /**
     * Computes the binomial coefficient.
     * The largest value of `n` for which all coefficients can
     * fit into a `long` is 66.
     *
     * @param n Size of the set.
     * @param k Size of the subsets to be counted.
     * @return `n choose k`.
     * @throws IllegalArgumentException if `n < 0`.
     * @throws IllegalArgumentException if `k > n`.
     * @throws ArithmeticException if the result is too large to be
     * represented by a `long`.
     */
    fun value(n: Int, k: Int): Long {
        checkBinomial(
            n,
            k
        )
        if (n == k ||
            k == 0
        ) {
            return 1
        }
        if (k == 1 ||
            k == n - 1
        ) {
            return n.toLong()
        }
        // Use symmetry for large k.
        if (k > n / 2) {
            return value(
                n,
                n - k
            )
        }

        // We use the formulae:
        // (n choose k) = n! / (n-k)! / k!
        // (n choose k) = ((n-k+1)*...*n) / (1*...*k)
        // which can be written
        // (n choose k) = (n-1 choose k-1) * n / k
        var result: Long = 1
        if (n <= 61) {
            // For n <= 61, the naive implementation cannot overflow.
            var i = n - k + 1
            for (j in 1..k) {
                result = result * i / j
                i++
            }
        } else if (n <= 66) {
            // For n > 61 but n <= 66, the result cannot overflow,
            // but we must take care not to overflow intermediate values.
            var i = n - k + 1
            for (j in 1..k) {
                // We know that (result * i) is divisible by j,
                // but (result * i) may overflow, so we split j:
                // Filter out the gcd, d, so j/d and i/d are integer.
                // result is divisible by (j/d) because (j/d)
                // is relative prime to (i/d) and is a divisor of
                // result * (i/d).
                val d: Long = ArithmeticUtils.gcd(i, j).toLong()
                result = result / (j / d) * (i / d)
                ++i
            }
        } else {
            // For n > 66, a result overflow might occur, so we check
            // the multiplication, taking care to not overflow
            // unnecessary.
            var i = n - k + 1
            for (j in 1..k) {
                val d: Long = ArithmeticUtils.gcd(i, j).toLong()
                result = multiplyExact(result / (j / d), i / d)
                ++i
            }
        }
        return result
    }

    /**
     * Check binomial preconditions.
     *
     * @param n Size of the set.
     * @param k Size of the subsets to be counted.
     * @throws IllegalArgumentException if `n < 0`.
     * @throws IllegalArgumentException if `k > n` or `k < 0`.
     */
    fun checkBinomial(
        n: Int,
        k: Int
    ) {
        if (n < 0) {
            throw CombinatoricsException(
                CombinatoricsException.NEGATIVE(
                    n
                )
            )
        }
        if (k > n ||
            k < 0
        ) {
            throw CombinatoricsException(
                CombinatoricsException.OUT_OF_RANGE(
                    k,
                    0,
                    n
                )
            )
        }
    }
}
