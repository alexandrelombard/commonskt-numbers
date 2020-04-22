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

import kotlin.math.abs

/**
 * [Error function](http://mathworld.wolfram.com/Erf.html).
 */
object Erf {
    /** The threshold value for returning the extreme value.  */
    private const val EXTREME_VALUE_BOUND = 40.0

    /**
     *
     *
     * This implementation computes erf(x) using the
     * [regularized gamma function][RegularizedGamma.P.value],
     * following [ Erf](http://mathworld.wolfram.com/Erf.html), equation (3)
     *
     *
     *
     *
     * The returned value is always between -1 and 1 (inclusive).
     * If `abs(x) > 40`, then `Erf.value(x)` is indistinguishable from
     * either 1 or -1 at `double` precision, so the appropriate extreme value
     * is returned.
     *
     *
     * @param x the value.
     * @return the error function.
     * @throws ArithmeticException if the algorithm fails to converge.
     *
     * @see RegularizedGamma.P.value
     */
    fun value(x: Double): Double {
        if (abs(x) > EXTREME_VALUE_BOUND) {
            return (if (x > 0) 1 else -1).toDouble()
        }
        val ret: Double = RegularizedGamma.P.value(0.5, x * x, 1e-15, 10000)
        return if (x < 0) -ret else ret
    }
}
