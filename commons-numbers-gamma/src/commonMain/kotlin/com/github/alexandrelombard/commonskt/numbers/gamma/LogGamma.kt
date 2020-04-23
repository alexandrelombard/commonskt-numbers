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

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln

/**
 * Function \( \ln \Gamma(x) \).
 *
 * Class is immutable.
 */
object LogGamma {
    /** Lanczos constant.  */
    private const val LANCZOS_G = 607.0 / 128.0

    /** Performance.  */
    private val HALF_LOG_2_PI: Double = 0.5 * ln(2.0 * PI)

    /**
     * Computes the function \( \ln \Gamma(x) \) for `x >= 0`.
     *
     * For `x <= 8`, the implementation is based on the double precision
     * implementation in the *NSWC Library of Mathematics Subroutines*,
     * `DGAMLN`. For `x >= 8`, the implementation is based on
     *
     *  * [Gamma
 * Function](http://mathworld.wolfram.com/GammaFunction.html), equation (28).
     *  * [
 * Lanczos Approximation](http://mathworld.wolfram.com/LanczosApproximation.html), equations (1) through (5).
     *  * [Paul Godfrey, A note on
 * the computation of the convergent Lanczos complex Gamma
 * approximation](http://my.fit.edu/~gabdo/gamma.txt)
     *
     *
     * @param x Argument.
     * @return \( \ln \Gamma(x) \), or `NaN` if `x <= 0`.
     */
    fun value(x: Double): Double {
        return if (x.isNaN() || x <= 0.0) {
            Double.NaN
        } else if (x < 0.5) {
            LogGamma1p.value(x) - ln(x)
        } else if (x <= 2.5) {
            LogGamma1p.value(x - 0.5 - 0.5)
        } else if (x <= 8.0) {
            val n = floor(x - 1.5) as Int
            var prod = 1.0
            for (i in 1..n) {
                prod *= x - i
            }
            LogGamma1p.value(x - (n + 1)) + ln(prod)
        } else {
            val sum: Double =
                LanczosApproximation.value(x)
            val tmp = x + LANCZOS_G + .5
            (x + .5) * ln(tmp) - tmp +
                    HALF_LOG_2_PI + ln(sum / x)
        }
    }
}