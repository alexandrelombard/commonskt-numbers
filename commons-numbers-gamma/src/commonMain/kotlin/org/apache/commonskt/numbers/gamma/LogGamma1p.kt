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

import kotlin.math.ln1p

/**
 * Function \( \ln \Gamma(1 + x) \).
 *
 * Class is immutable.
 */
internal object LogGamma1p {
    /**
     * Computes the function \( \ln \Gamma(1 + x) \) for \( -0.5 \leq x \leq 1.5 \).
     *
     * This implementation is based on the double precision implementation in
     * the *NSWC Library of Mathematics Subroutines*, `DGMLN1`.
     *
     * @param x Argument.
     * @return \( \ln \Gamma(1 + x) \)
     * @throws IllegalArgumentException if `x < -0.5` or `x > 1.5`.
     */
    fun value(x: Double): Double {
        if (x < -0.5 || x > 1.5) {
            throw GammaException(GammaException.OUT_OF_RANGE(x, -0.5, 1.5))
        }
        return -ln1p(InvGamma1pm1.value(x))
    }
}