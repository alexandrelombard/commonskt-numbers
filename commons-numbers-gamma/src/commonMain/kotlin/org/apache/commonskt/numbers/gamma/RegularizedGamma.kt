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
package org.apache.commonskt.numbers.gamma

import org.apache.commonskt.numbers.fraction.ContinuedFraction
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * [
 * Regularized Gamma functions](http://mathworld.wolfram.com/RegularizedGammaFunction.html).
 *
 * Class is immutable.
 */
object RegularizedGamma {
    /** Maximum allowed numerical error.  */
    private const val DEFAULT_EPSILON = 1e-15

    /**
     * \( P(a, x) \) [
     * regularized Gamma function](http://mathworld.wolfram.com/RegularizedGammaFunction.html).
     *
     * Class is immutable.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    object P {
        /**
         * Computes the regularized gamma function \( P(a, x) \).
         *
         * The implementation of this method is based on:
         *
         *
         * [Regularized Gamma Function](http://mathworld.wolfram.com/RegularizedGammaFunction.html), equation (1)
         * [Incomplete Gamma Function](http://mathworld.wolfram.com/IncompleteGammaFunction.html), equation (4).
         * [Confluent Hypergeometric Function of the First Kind](http://mathworld.wolfram.com/ConfluentHypergeometricFunctionoftheFirstKind.html), equation (1).
         *
         * @param a Argument.
         * @param x Argument.
         * @param epsilon Tolerance in continued fraction evaluation.
         * @param maxIterations Maximum number of iterations in continued fraction evaluation.
         * @return \( P(a, x) \).
         * @throws ArithmeticException if the continued fraction fails to converge.
         */
        fun value(
            a: Double,
            x: Double,
            epsilon: Double = DEFAULT_EPSILON,
            maxIterations: Int = Int.MAX_VALUE
        ): Double {
            return if (a.isNaN() ||
                x.isNaN() || a <= 0 || x < 0) {
                Double.NaN
            } else if (x == 0.0) {
                0.0
            } else if (x >= a + 1) {
                // Q should converge faster in this case.
                1.0 - Q.value(a, x, epsilon, maxIterations)
            } else {
                // Series.
                var n = 0.0 // current element index
                var an = 1 / a // n-th element in the series
                var sum = an // partial sum
                while (abs(an / sum) > epsilon && n < maxIterations && sum < Double.POSITIVE_INFINITY
                ) {
                    // compute next element in the series
                    n += 1.0
                    an *= x / (a + n)

                    // update partial sum
                    sum += an
                }
                if (n >= maxIterations) {
                    throw ArithmeticException("Failed to converge within $maxIterations iterations")
                } else if (sum.isInfinite()) {
                    1.0
                } else {
                    // Ensure result is in the range [0, 1]
                    val result: Double =
                        exp(-x + a * ln(x) - LogGamma.value(a)) * sum
                    if (result > 1.0) 1.0 else result
                }
            }
        }
    }

    /**
     * Creates the \( Q(a, x) \equiv 1 - P(a, x) \)
     * [regularized Gamma function](http://mathworld.wolfram.com/RegularizedGammaFunction.html).
     *
     * Class is immutable.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    object Q {
        /**
         * Computes the regularized gamma function \( Q(a, x) = 1 - P(a, x) \).
         *
         * The implementation of this method is based on:
         *
         * [Regularized Gamma Function](http://mathworld.wolfram.com/RegularizedGammaFunction.html), equation (1).
         * [Regularized incomplete gamma function: Continued fraction representations
         * (formula 06.08.10.0003)](http://functions.wolfram.com/GammaBetaErf/GammaRegularized/10/0003/)
         *
         * @param a Argument.
         * @param x Argument.
         * @param epsilon Tolerance in continued fraction evaluation.
         * @param maxIterations Maximum number of iterations in continued fraction evaluation.
         * @throws ArithmeticException if the continued fraction fails to converge.
         * @return \( Q(a, x) \).
         */
        fun value(
            a: Double,
            x: Double,
            epsilon: Double = DEFAULT_EPSILON,
            maxIterations: Int = Int.MAX_VALUE
        ): Double {
            return if (a.isNaN() || x.isNaN() || a <= 0 || x < 0) {
                Double.NaN
            } else if (x == 0.0) {
                1.0
            } else if (x < a + 1) {
                // P should converge faster in this case.
                1.0 - P.value(a, x, epsilon, maxIterations)
            } else {
                val cf: ContinuedFraction = object : ContinuedFraction() {
                    /** {@inheritDoc}  */
                    override fun getA(n: Int, x: Double): Double {
                        return n * (a - n)
                    }

                    /** {@inheritDoc}  */
                    override fun getB(n: Int, x: Double): Double {
                        return 2 * n + 1 - a + x
                    }
                }
                exp(-x + a * ln(x) - LogGamma.value(a)) /
                        cf.evaluate(x, epsilon, maxIterations)
            }
        }
    }
}