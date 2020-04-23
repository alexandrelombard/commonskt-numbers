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
package com.github.alexandrelombard.commonskt.numbers.gamma

import com.github.alexandrelombard.commonskt.numbers.fraction.ContinuedFraction
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.ln1p

/**
 * [
 * Regularized Beta function](http://mathworld.wolfram.com/RegularizedBetaFunction.html).
 *
 *
 * This class is immutable.
 *
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object RegularizedBeta {
    /** Maximum allowed numerical error.  */
    private const val DEFAULT_EPSILON = 1e-14
    /**
     * Computes the value of the
     * [Regularized beta function](http://mathworld.wolfram.com/RegularizedBetaFunction.html) I(x, a, b).
     *
     * The implementation of this method is based on:
     *
     * [Regularized Beta Function](http://mathworld.wolfram.com/RegularizedBetaFunction.html).
     * [Regularized Beta Function](http://functions.wolfram.com/06.21.10.0001.01).
     *
     * @param x the value.
     * @param a Parameter `a`.
     * @param b Parameter `b`.
     * @param epsilon When the absolute value of the nth item in the
     * series is less than epsilon the approximation ceases to calculate
     * further elements in the series.
     * @param maxIterations Maximum number of "iterations" to complete.
     * @return the regularized beta function I(x, a, b).
     * @throws ArithmeticException if the algorithm fails to converge.
     */
    fun value(
        x: Double,
        a: Double,
        b: Double,
        epsilon: Double = DEFAULT_EPSILON,
        maxIterations: Int = Int.MAX_VALUE
    ): Double {
        return if (x.isNaN() ||
            a.isNaN() ||
            b.isNaN() || x < 0 || x > 1 || a <= 0 || b <= 0
        ) {
            Double.NaN
        } else if (x > (a + 1) / (2 + b + a) &&
            1 - x <= (b + 1) / (2 + b + a)
        ) {
            1 - value(
                1 - x,
                b,
                a,
                epsilon,
                maxIterations
            )
        } else {
            val fraction: ContinuedFraction = object : ContinuedFraction() {
                /** {@inheritDoc}  */
                override fun getA(n: Int, x: Double): Double {
                    return if (n % 2 == 0) { // even
                        val m = n / 2.0
                        m * (b - m) * x /
                                ((a + 2 * m - 1) * (a + 2 * m))
                    } else {
                        val m = (n - 1.0) / 2.0
                        -((a + m) * (a + b + m) * x) /
                                ((a + 2 * m) * (a + 2 * m + 1))
                    }
                }

                /** {@inheritDoc}  */
                override fun getB(n: Int, x: Double): Double {
                    return 1.0
                }
            }
            exp(
                a * ln(x) + b * ln1p(-x) -
                        ln(a) - LogBeta.value(
                    a,
                    b
                )
            ) /
                    fraction.evaluate(x, epsilon, maxIterations)
        }
    }
}