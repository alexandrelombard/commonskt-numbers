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

import kotlin.math.*

/**
 * [Gamma
 * function](http://mathworld.wolfram.com/GammaFunction.html).
 *
 *
 * The [gamma
 * function](http://mathworld.wolfram.com/GammaFunction.html) can be seen to extend the factorial function to cover real and
 * complex numbers, but with its argument shifted by `-1`. This
 * implementation supports real numbers.
 *
 *
 *
 * This class is immutable.
 *
 */
object Gamma {
    /** The threshold value for choosing the Lanczos approximation.  */
    private const val LANCZOS_THRESHOLD = 20.0

    /** (2).  */
    private const val SQRT_TWO_PI = 2.506628274631000502

    /**
     * Computes the value of \( \Gamma(x) \).
     *
     *
     * Based on the *NSWC Library of Mathematics Subroutines* double
     * precision implementation, `DGAMMA`.
     *
     * @param x Argument.
     * @return \( \Gamma(x) \)
     */
    fun value(x: Double): Double {
        if (x == round(x) && x <= 0.0) {
            return Double.NaN
        }
        val absX: Double = abs(x)
        return if (absX <= LANCZOS_THRESHOLD) {
            if (x >= 1) {
                /*
                 * From the recurrence relation
                 * Gamma(x) = (x - 1) * ... * (x - n) * Gamma(x - n),
                 * then
                 * Gamma(t) = 1 / [1 + InvGamma1pm1.value(t - 1)],
                 * where t = x - n. This means that t must satisfy
                 * -0.5 <= t - 1 <= 1.5.
                 */
                var prod = 1.0
                var t = x
                while (t > 2.5) {
                    t -= 1.0
                    prod *= t
                }
                prod / (1 + InvGamma1pm1.value(t - 1))
            } else {
                /*
                             * From the recurrence relation
                             * Gamma(x) = Gamma(x + n + 1) / [x * (x + 1) * ... * (x + n)]
                             * then
                             * Gamma(x + n + 1) = 1 / [1 + InvGamma1pm1.value(x + n)],
                             * which requires -0.5 <= x + n <= 1.5.
                             */
                var prod = x
                var t = x
                while (t < -0.5) {
                    t += 1.0
                    prod *= t
                }
                1 / (prod * (1 + InvGamma1pm1.value(t)))
            }
        } else {
            val y: Double = absX + LanczosApproximation.g() + 0.5
            val gammaAbs: Double = SQRT_TWO_PI / absX *
                    y.pow(absX + 0.5) *
                    exp(-y) * LanczosApproximation.value(absX)
            if (x > 0) {
                gammaAbs
            } else {
                /*
                             * From the reflection formula
                             * Gamma(x) * Gamma(1 - x) * sin(pi * x) = pi,
                             * and the recurrence relation
                             * Gamma(1 - x) = -x * Gamma(-x),
                             * it is found
                             * Gamma(x) = -pi / [x * sin(pi * x) * Gamma(-x)].
                             */
                -PI / (x * sin(PI * x) * gammaAbs)
            }
        }
    }
}
