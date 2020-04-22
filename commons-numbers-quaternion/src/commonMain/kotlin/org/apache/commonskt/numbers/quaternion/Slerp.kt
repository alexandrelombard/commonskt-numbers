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
package org.apache.commonskt.numbers.quaternion

import kotlin.math.acos
import kotlin.math.sin

/**
 * Perform spherical linear interpolation ([Slerp](https://en.wikipedia.org/wiki/Slerp)).
 *
 * The *Slerp* algorithm is designed to interpolate smoothly between
 * two rotations/orientations, producing a constant-speed motion along an arc.
 * The original purpose of this algorithm was to animate 3D rotations. All output
 * quaternions are in positive polar form, meaning a unit quaternion with a positive
 * scalar component.
 */
/**
 * @param start Start of the interpolation.
 * @param end End of the interpolation.
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Slerp(
    start: Quaternion,
    end: Quaternion
) : (Double)->Quaternion {
    /** Start of the interpolation.  */
    private val start: Quaternion

    /** End of the interpolation.  */
    private val end: Quaternion

    /** Linear or spherical interpolation algorithm.  */
    private val algo: (Double)->Quaternion

    /**
     * Performs the interpolation.
     * The rotation returned by this method is controlled by the interpolation parameter, `t`.
     * All other values are interpolated (or extrapolated if `t` is outside of the
     * `[0, 1]` range). The returned quaternion is in positive polar form, meaning that it
     * is a unit quaternion with a positive scalar component.
     *
     * @param t Interpolation control parameter.
     * When `t = 0`, a rotation equal to the start instance is returned.
     * When `t = 1`, a rotation equal to the end instance is returned.
     * @return an interpolated quaternion in positive polar form.
     */
    override fun invoke(t: Double): Quaternion {
        // Handle no-op cases.
        if (t == 0.0) {
            return start
        } else if (t == 1.0) {
            // Call to "positivePolarForm()" is required because "end" might
            // not be in positive polar form.
            return end.positivePolarForm()
        }
        return algo.invoke(t)
    }

    /**
     * Linear interpolation, used when the quaternions are too closely aligned.
     */
    private inner class Linear : (Double)->Quaternion {
        /** {@inheritDoc}  */
        override fun invoke(t: Double): Quaternion {
            val f = 1 - t
            return Quaternion.of(
                f * start.w + t * end.w,
                f * start.x + t * end.x,
                f * start.y + t * end.y,
                f * start.z + t * end.z
            ).positivePolarForm()
        }
    }

    /**
     * Spherical interpolation, used when the quaternions are too closely aligned.
     * When we may end up dividing by zero (cf. 1/sin(theta) term below).
     * [Linear] interpolation must be used.
     */
    private inner class Spherical
        /**
         * @param dot Dot product of the start and end quaternions.
         */
        internal constructor(dot: Double) : (Double)->Quaternion {
        /** Angle of rotation.  */
        private val theta: Double

        /** Sine of [.theta].  */
        private val sinTheta: Double

        /** {@inheritDoc}  */
        override fun invoke(t: Double): Quaternion {
            val f1: Double = sin((1 - t) * theta) / sinTheta
            val f2: Double = sin(t * theta) / sinTheta
            return Quaternion.of(
                f1 * start.w + f2 * end.w,
                f1 * start.x + f2 * end.x,
                f1 * start.y + f2 * end.y,
                f1 * start.z + f2 * end.z
            ).positivePolarForm()
        }

        init {
            theta = acos(dot)
            sinTheta = sin(theta)
        }
    }

    companion object {
        /**
         * Threshold max value for the dot product.
         * If the quaternion dot product is greater than this value (i.e. the
         * quaternions are very close to each other), then the quaternions are
         * linearly interpolated instead of spherically interpolated.
         */
        private const val MAX_DOT_THRESHOLD = 0.9995
    }

    init {
        this.start = start.positivePolarForm()
        val e = end.positivePolarForm()
        var dot = this.start.dot(e)

        // If the dot product is negative, then the interpolation won't follow the shortest
        // angular path between the two quaterions. In this case, invert the end quaternion
        // to produce an equivalent rotation that will give us the path we want.
        if (dot < 0) {
            dot = -dot
            this.end = e.negate()
        } else {
            this.end = e
        }
        algo = if (dot > MAX_DOT_THRESHOLD) Linear() else Spherical(dot)
    }
}
