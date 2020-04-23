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

import com.github.alexandrelombard.commonskt.numbers.combinatorics.BinomialCoefficient.checkBinomial
import kotlin.math.ln

/**
 * Natural logarithm of the [
 * binomial coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html).
 * It is "`n choose k`", the number of `k`-element subsets that
 * can be selected from an `n`-element set.
 */
@ExperimentalStdlibApi
object LogBinomialCoefficient {
    /**
     * Computes the logarithm of the binomial coefficient.
     * The largest value of `n` for which all coefficients can
     * fit into a `long` is 66.
     *
     * @param n Size of the set.
     * @param k Size of the subsets to be counted.
     * @return `log(n choose k)`.
     * @throws IllegalArgumentException if `n < 0`.
     * @throws IllegalArgumentException if `k > n`.
     */
    @ExperimentalUnsignedTypes
    fun value(n: Int, k: Int): Double {
        checkBinomial(n, k)
        if (n == k ||
            k == 0
        ) {
            return 0.0
        }
        if (k == 1 ||
            k == n - 1
        ) {
            return ln(n.toDouble())
        }

        // For values small enough to do exact integer computation,
        // return the log of the exact value.
        if (n < 67) {
            return ln(
                BinomialCoefficient.value(
                    n,
                    k
                ).toDouble())
        }

        // Logarithm of "BinomialCoefficientDouble" for values that
        // will not overflow.
        if (n < 1030) {
            return ln(
                BinomialCoefficientDouble.value(
                    n,
                    k
                )
            )
        }
        if (k > n / 2) {
            return value(
                n,
                n - k
            )
        }

        // Sum for values that could overflow.
        var logSum = 0.0

        // n! / (n - k)!
        for (i in n - k + 1..n) {
            logSum += ln(i.toDouble())
        }

        // Divide by k!
        for (i in 2..k) {
            logSum -= ln(i.toDouble())
        }
        return logSum
    }
}
