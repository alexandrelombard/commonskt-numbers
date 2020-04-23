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

import kotlin.math.floor

/**
 * Represents the [angle](https://en.wikipedia.org/wiki/Angle) concept.
 */
class PlaneAngle
    /**
     * @param value Value in turns.
     */
    private constructor(
        /** Value (in turns).  */
        private val value: Double) {

    /**
     * @return the value in [turns](https://en.wikipedia.org/wiki/Turn_%28geometry%29).
     */
    fun toTurns(): Double {
        return value
    }

    /**
     * @return the value in [radians](https://en.wikipedia.org/wiki/Radian).
     */
    fun toRadians(): Double {
        return value * TO_RADIANS
    }

    /**
     * @return the value in [degrees](https://en.wikipedia.org/wiki/Degree_%28angle%29).
     */
    fun toDegrees(): Double {
        return value * TO_DEGREES
    }

    /**
     * Normalize an angle in an interval of size 1 turn around a
     * center value.
     *
     * @param center Center of the desired interval for the result.
     * @return `a - k` with integer `k` such that
     * `center - 0.5 <= a - k < center + 0.5` (in turns).
     */
    fun normalize(center: PlaneAngle): PlaneAngle {
        val lowerBound = center.value - HALF_TURN
        val upperBound = center.value + HALF_TURN
        val normalized: Double = value - floor(value - lowerBound)
        return if (normalized < upperBound) PlaneAngle(
            normalized
        ) else  // If value is too small to be representable compared to the
        // floor expression above (ie, if value + x = x), then we may
        // end up with a number exactly equal to the upper bound here.
        // In that case, subtract one from the normalized value so that
        // we can fulfill the contract of only returning results strictly
        // less than the upper bound.
            PlaneAngle(normalized - 1)
    }

    /**
     * Test for equality with another object.
     * Objects are considered to be equal if the two values are exactly the
     * same, or both are `Double.NaN`.
     *
     * @param other Object to test for equality with this instance.
     * @return `true` if the objects are equal, `false` if
     * `other` is `null`, not an instance of `PlaneAngle`,
     * or not equal to this instance.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is PlaneAngle) {
            value.toBits() ==
                    other.value.toBits()
        } else false
    }

    /** {@inheritDoc}  */
    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        /** Zero.  */
        val ZERO = PlaneAngle(0.0)

        /** Half-turn (aka  radians).  */
        val PI = PlaneAngle(0.5)

        /** Conversion factor.  */
        private const val HALF_TURN = 0.5

        /** Conversion factor.  */
        private val TO_RADIANS: Double =
            PlaneAngleRadians.TWO_PI

        /** Conversion factor.  */
        private val FROM_RADIANS = 1.0 / TO_RADIANS

        /** Conversion factor.  */
        private const val TO_DEGREES = 360.0

        /** Conversion factor.  */
        private const val FROM_DEGREES = 1.0 / TO_DEGREES

        /**
         * @param angle (in [turns](https://en.wikipedia.org/wiki/Turn_%28geometry%29)).
         * @return a new intance.
         */
        fun ofTurns(angle: Double): PlaneAngle {
            return PlaneAngle(angle)
        }

        /**
         * @param angle (in [radians](https://en.wikipedia.org/wiki/Radian)).
         * @return a new intance.
         */
        fun ofRadians(angle: Double): PlaneAngle {
            return PlaneAngle(angle * FROM_RADIANS)
        }

        /**
         * @param angle (in [degrees](https://en.wikipedia.org/wiki/Degree_%28angle%29)).
         * @return a new intance.
         */
        fun ofDegrees(angle: Double): PlaneAngle {
            return PlaneAngle(angle * FROM_DEGREES)
        }
    }

}
