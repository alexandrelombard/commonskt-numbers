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
package com.github.alexandrelombard.commonskt.numbers.angle

/**
 * Utility class where all `double` values are assumed to be in
 * radians.
 *
 * @see PlaneAngle
 */
object PlaneAngleRadians {
    /** Value of \( \pi \): {@value}.  */
    val PI: Double = kotlin.math.PI

    /** Value of \( 2\pi \): {@value}.  */
    val TWO_PI = 2 * PI

    /** Value of \( \pi/2 \): {@value}.  */
    val PI_OVER_TWO = 0.5 * PI

    /** Value of \( 3\pi/2 \): {@value}.  */
    val THREE_PI_OVER_TWO = 3 * PI_OVER_TWO

    /**
     * Normalize an angle in an interval of size 2 around a
     * center value.
     *
     * @param angle Value to be normalized.
     * @param center Center of the desired interval for the result.
     * @return `a - 2 * k` with integer `k` such that
     * `center - pi <= a - 2 * k * pi < center + pi`.
     */
    fun normalize(
        angle: Double,
        center: Double
    ): Double {
        val a =
            PlaneAngle.ofRadians(angle)
        val c =
            PlaneAngle.ofRadians(center)
        return a.normalize(c).toRadians()
    }

    /**
     * Normalize an angle to be in the range [-, ).
     *
     * @param angle Value to be normalized.
     * @return `a - 2 * k` with integer `k` such that
     * `-pi <= a - 2 * k * pi < pi`.
     */
    fun normalizeBetweenMinusPiAndPi(angle: Double): Double {
        return PlaneAngle.ofRadians(
            angle
        ).normalize(PlaneAngle.ZERO).toRadians()
    }

    /**
     * Normalize an angle to be in the range [0, 2).
     *
     * @param angle Value to be normalized.
     * @return `a - 2 * k` with integer `k` such that
     * `0 <= a - 2 * k * pi < 2 * pi`.
     */
    fun normalizeBetweenZeroAndTwoPi(angle: Double): Double {
        return PlaneAngle.ofRadians(
            angle
        ).normalize(PlaneAngle.PI).toRadians()
    }
}
