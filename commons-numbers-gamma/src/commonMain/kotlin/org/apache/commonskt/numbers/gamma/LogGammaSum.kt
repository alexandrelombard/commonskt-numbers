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

import kotlin.math.ln
import kotlin.math.ln1p

/**
 * Computes \( \log_e(\Gamma(a+b)) \).
 *
 *
 * This class is immutable.
 *
 */
internal object LogGammaSum {
    /**
     * Computes the value of log Γ(a + b) for 1 ≤ a, b ≤ 2.
     * Based on the *NSWC Library of Mathematics Subroutines*
     * implementation, `DGSMLN`.
     *
     * @param a First argument.
     * @param b Second argument.
     * @return the value of `log(Gamma(a + b))`.
     * @throws IllegalArgumentException if `a` or `b` is lower than 1
     * or larger than 2.
     */
    fun value(
        a: Double,
        b: Double
    ): Double {
        if (a < 1 ||
            a > 2
        ) {
            throw GammaException(GammaException.OUT_OF_RANGE(a, 1.0, 2.0))
        }
        if (b < 1 ||
            b > 2
        ) {
            throw GammaException(GammaException.OUT_OF_RANGE(b, 1.0, 2.0))
        }
        val x = a - 1 + (b - 1)
        return if (x <= 0.5) {
            LogGamma1p.value(1 + x)
        } else if (x <= 1.5) {
            LogGamma1p.value(x) + ln1p(x)
        } else {
            LogGamma1p.value(x - 1) + ln(x * (1 + x))
        }
    }
}