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

import org.apache.commonskt.numbers.core.Precision
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * This class implements [
 * quaternions](http://mathworld.wolfram.com/Quaternion.html) (Hamilton's hypercomplex numbers).
 *
 *
 * Wherever quaternion components are listed in sequence, this class follows the
 * convention of placing the scalar (`w`) component first, e.g. [`w, x, y, z`].
 * Other libraries and textbooks may place the `w` component last.
 *
 *
 * Instances of this class are guaranteed to be immutable.
 */
@Suppress("unused")
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Quaternion {
    /** For enabling specialized method implementations.  */
    private val type: Type

    /**
     * Gets the first component of the quaternion (scalar part).
     *
     * @return the scalar part.
     */
    /** First component (scalar part).  */
    val w: Double

    /**
     * Gets the second component of the quaternion (first component
     * of the vector part).
     *
     * @return the first component of the vector part.
     */
    /** Second component (first vector part).  */
    val x: Double

    /**
     * Gets the third component of the quaternion (second component
     * of the vector part).
     *
     * @return the second component of the vector part.
     */
    /** Third component (second vector part).  */
    val y: Double

    /**
     * Gets the fourth component of the quaternion (third component
     * of the vector part).
     *
     * @return the third component of the vector part.
     */
    /** Fourth component (third vector part).  */
    val z: Double

    /**
     * For enabling optimized implementations.
     */
    /**
     * @param normSq `normSq` method.
     * @param norm `norm` method.
     * @param isUnit `isUnit` method.
     */
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    private enum class Type(
        normSq: (Quaternion)->Double,
        norm: (Quaternion)->Double,
        isUnit: (Quaternion, Double)->Boolean
    ) {
        /** Default implementation.  */
        DEFAULT(
            Default.NORMSQ,
            Default.NORM,
            Default.IS_UNIT
        ),

        /** Quaternion has unit norm.  */
        NORMALIZED(
            Normalized.NORM,
            Normalized.NORM,
            Normalized.IS_UNIT
        ),

        /** Quaternion has positive scalar part.  */
        POSITIVE_POLAR_FORM(
            Normalized.NORM,
            Normalized.NORM,
            Normalized.IS_UNIT
        );

        /** [Quaternion.normSq].  */
        private val normSq: (Quaternion)->Double

        /** [Quaternion.norm].  */
        private val norm: (Quaternion)->Double

        /** [Quaternion.isUnit].  */
        private val testIsUnit: (Quaternion, Double)->Boolean

        /** Default implementations.  */
        @ExperimentalUnsignedTypes
        @ExperimentalStdlibApi
        private object Default {
            /** [Quaternion.normSq].  */
            val NORMSQ: (Quaternion)->Double =
                { q: Quaternion -> q.w * q.w + q.x * q.x + q.y * q.y + q.z * q.z }

            /** [Quaternion.norm].  */
            val NORM: (Quaternion)->Double = { q: Quaternion ->
                sqrt(
                    NORMSQ.invoke(q)
                )
            }

            /** [Quaternion.isUnit].  */
            val IS_UNIT: (Quaternion, Double)->Boolean =
                { q: Quaternion, eps: Double ->
                    Precision.equals(
                        NORM.invoke(q),
                        1.0,
                        eps
                    )
                }
        }

        /** Implementations for normalized quaternions.  */
        private object Normalized {
            /** [Quaternion.norm] returns 1.  */
            val NORM: (Quaternion)->Double = { q: Quaternion -> 1.0 }

            /** [Quaternion.isUnit] returns 1.  */
            val IS_UNIT: (Quaternion, Double)->Boolean =
                { q: Quaternion, eps: Double -> true }
        }

        /**
         * @param q Quaternion.
         * @return the norm squared.
         */
        fun normSq(q: Quaternion): Double {
            return normSq.invoke(q)
        }

        /**
         * @param q Quaternion.
         * @return the norm.
         */
        fun norm(q: Quaternion): Double {
            return norm.invoke(q)
        }

        /**
         * @param q Quaternion.
         * @param eps Tolerance.
         * @return whether `q` has unit norm within the allowed tolerance.
         */
        fun isUnit(
            q: Quaternion,
            eps: Double
        ): Boolean {
            return testIsUnit.invoke(q, eps)
        }

        init {
            this.normSq = normSq
            this.norm = norm
            testIsUnit = isUnit
        }
    }

    /**
     * Builds a quaternion from its components.
     *
     * @param type Quaternion type.
     * @param w Scalar component.
     * @param x First vector component.
     * @param y Second vector component.
     * @param z Third vector component.
     */
    private constructor(
        type: Type,
        w: Double,
        x: Double,
        y: Double,
        z: Double
    ) {
        this.type = type
        this.w = w
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Copies the given quaternion, but change its [Type].
     *
     * @param type Quaternion type.
     * @param q Quaternion whose components will be copied.
     */
    private constructor(
        type: Type,
        q: Quaternion
    ) {
        this.type = type
        w = q.w
        x = q.x
        y = q.y
        z = q.z
    }

    /**
     * Returns the conjugate of this quaternion number.
     * The conjugate of `a + bi + cj + dk` is `a - bi -cj -dk`.
     *
     * @return the conjugate of this quaternion object.
     */
    fun conjugate(): Quaternion {
        return of(w, -x, -y, -z)
    }

    /**
     * Returns the Hamilton product of the instance by a quaternion.
     *
     * @param q Quaternion.
     * @return the product of this instance with `q`, in that order.
     */
    fun multiply(q: Quaternion): Quaternion {
        return multiply(this, q)
    }

    /**
     * Computes the sum of the instance and another quaternion.
     *
     * @param q Quaternion.
     * @return the sum of this instance and `q`.
     */
    fun add(q: Quaternion): Quaternion {
        return add(this, q)
    }

    /**
     * Subtracts a quaternion from the instance.
     *
     * @param q Quaternion.
     * @return the difference between this instance and `q`.
     */
    fun subtract(q: Quaternion): Quaternion {
        return subtract(this, q)
    }

    /**
     * Computes the dot-product of the instance by a quaternion.
     *
     * @param q Quaternion.
     * @return the dot product of this instance and `q`.
     */
    fun dot(q: Quaternion): Double {
        return dot(this, q)
    }

    /**
     * Computes the norm of the quaternion.
     *
     * @return the norm.
     */
    fun norm(): Double {
        return type.norm(this)
    }

    /**
     * Computes the square of the norm of the quaternion.
     *
     * @return the square of the norm.
     */
    fun normSq(): Double {
        return type.normSq(this)
    }

    /**
     * Computes the normalized quaternion (the versor of the instance).
     * The norm of the quaternion must not be near zero.
     *
     * @return a normalized quaternion.
     * @throws IllegalStateException if the norm of the quaternion is NaN, infinite,
     * or near zero.
     */
    fun normalize(): Quaternion {
        return when (type) {
            Type.NORMALIZED, Type.POSITIVE_POLAR_FORM -> this
            Type.DEFAULT -> {
                val norm = norm()
                check(
                    !(norm < Precision.SAFE_MIN ||
                            !norm.isFinite())
                ) { ILLEGAL_NORM_MSG + norm }
                val unit = divide(norm)
                if (w >= 0) Quaternion(
                    Type.POSITIVE_POLAR_FORM, unit
                ) else Quaternion(Type.NORMALIZED, unit)
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is Quaternion) {
            val q = other as Quaternion?
            return w == q!!.w && x == q.x && y == q.y && z == q.z
        }
        return false
    }

    /**
     * {@inheritDoc}
     */
    override fun hashCode(): Int {
        return doubleArrayOf(w, x, y, z).hashCode()
    }

    /**
     * Checks whether this instance is equal to another quaternion
     * within a given tolerance.
     *
     * @param q Quaternion with which to compare the current quaternion.
     * @param eps Tolerance.
     * @return `true` if the each of the components are equal
     * within the allowed absolute error.
     */
    fun equals(
        q: Quaternion,
        eps: Double
    ): Boolean {
        return Precision.equals(w, q.w, eps) &&
                Precision.equals(x, q.x, eps) &&
                Precision.equals(y, q.y, eps) &&
                Precision.equals(z, q.z, eps)
    }

    /**
     * Checks whether the instance is a unit quaternion within a given
     * tolerance.
     *
     * @param eps Tolerance (absolute error).
     * @return `true` if the norm is 1 within the given tolerance,
     * `false` otherwise
     */
    fun isUnit(eps: Double): Boolean {
        return type.isUnit(this, eps)
    }

    /**
     * Checks whether the instance is a pure quaternion within a given
     * tolerance.
     *
     * @param eps Tolerance (absolute error).
     * @return `true` if the scalar part of the quaternion is zero.
     */
    fun isPure(eps: Double): Boolean {
        return abs(w) <= eps
    }

    /**
     * Returns the polar form of the quaternion.
     *
     * @return the unit quaternion with positive scalar part.
     */
    fun positivePolarForm(): Quaternion {
        return when (type) {
            Type.POSITIVE_POLAR_FORM -> this
            Type.NORMALIZED -> if (w >= 0) Quaternion(
                Type.POSITIVE_POLAR_FORM,
                this
            ) else Quaternion(Type.POSITIVE_POLAR_FORM, negate())
            Type.DEFAULT -> if (w >= 0) normalize() else  // The quaternion of rotation (normalized quaternion) q and -q
            // are equivalent (i.e. represent the same rotation).
                negate().normalize()
        }
    }

    /**
     * Returns the opposite of this instance.
     *
     * @return the quaternion for which all components have an opposite
     * sign to this one.
     */
    fun negate(): Quaternion {
        return when (type) {
            Type.POSITIVE_POLAR_FORM, Type.NORMALIZED -> Quaternion(
                Type.NORMALIZED,
                -w,
                -x,
                -y,
                -z
            )
            Type.DEFAULT -> Quaternion(Type.DEFAULT, -w, -x, -y, -z)
        }
    }

    /**
     * Returns the inverse of this instance.
     * The norm of the quaternion must not be zero.
     *
     * @return the inverse.
     * @throws IllegalStateException if the norm (squared) of the quaternion is NaN,
     * infinite, or near zero.
     */
    fun inverse(): Quaternion {
        return when (type) {
            Type.POSITIVE_POLAR_FORM, Type.NORMALIZED -> Quaternion(type, w, -x, -y, -z)
            Type.DEFAULT -> {
                val squareNorm = normSq()
                check(
                    !(squareNorm < Precision.SAFE_MIN ||
                            !squareNorm.isFinite())
                ) { ILLEGAL_NORM_MSG + sqrt(squareNorm) }
                of(
                    w / squareNorm,
                    -x / squareNorm,
                    -y / squareNorm,
                    -z / squareNorm
                )
            }
        }
    }

    /**
     * Gets the scalar part of the quaternion.
     *
     * @return the scalar part.
     * @see .getW
     */
    val scalarPart: Double
        get() = w

    /**
     * Gets the three components of the vector part of the quaternion.
     *
     * @return the vector part.
     * @see .getX
     * @see .getY
     * @see .getZ
     */
    val vectorPart: DoubleArray?
        get() = doubleArrayOf(x, y, z)

    /**
     * Multiplies the instance by a scalar.
     *
     * @param alpha Scalar factor.
     * @return a scaled quaternion.
     */
    fun multiply(alpha: Double): Quaternion {
        return of(
            alpha * w,
            alpha * x,
            alpha * y,
            alpha * z
        )
    }

    /**
     * Divides the instance by a scalar.
     *
     * @param alpha Scalar factor.
     * @return a scaled quaternion.
     */
    fun divide(alpha: Double): Quaternion {
        return of(
            w / alpha,
            x / alpha,
            y / alpha,
            z / alpha
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        val s = StringBuilder()
        s.append(FORMAT_START)
            .append(w).append(FORMAT_SEP)
            .append(x).append(FORMAT_SEP)
            .append(y).append(FORMAT_SEP)
            .append(z)
            .append(FORMAT_END)
        return s.toString()
    }

    /** See [.parse].  */
    private class QuaternionParsingException
    /**
     * @param msg Error message.
     */
    internal constructor(msg: String) : NumberFormatException(msg)

    companion object {
        /** Zero quaternion.  */
        val ZERO = of(0.0, 0.0, 0.0, 0.0)

        /** Identity quaternion.  */
        val ONE: Quaternion = Quaternion(Type.POSITIVE_POLAR_FORM, 1.0, 0.0, 0.0, 0.0)

        /** i.  */
        val I: Quaternion = Quaternion(Type.POSITIVE_POLAR_FORM, 0.0, 1.0, 0.0, 0.0)

        /** j.  */
        val J: Quaternion = Quaternion(Type.POSITIVE_POLAR_FORM, 0.0, 0.0, 1.0, 0.0)

        /** k.  */
        val K: Quaternion = Quaternion(Type.POSITIVE_POLAR_FORM, 0.0, 0.0, 0.0, 1.0)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 20170118L

        /** Error message.  */
        private val ILLEGAL_NORM_MSG: String? = "Illegal norm: "

        /** [String representation][.toString].  */
        private val FORMAT_START: String? = "["

        /** [String representation][.toString].  */
        private val FORMAT_END: String? = "]"

        /** [String representation][.toString].  */
        private val FORMAT_SEP: String? = " "

        /** The number of dimensions for the vector part of the quaternion.  */
        private const val VECTOR_DIMENSIONS = 3

        /** The number of parts when parsing a text representation of the quaternion.  */
        private const val NUMBER_OF_PARTS = 4

        /**
         * Builds a quaternion from its components.
         *
         * @param w Scalar component.
         * @param x First vector component.
         * @param y Second vector component.
         * @param z Third vector component.
         * @return a quaternion instance.
         */
        fun of(
            w: Double,
            x: Double,
            y: Double,
            z: Double
        ): Quaternion {
            return Quaternion(
                Type.DEFAULT,
                w, x, y, z
            )
        }

        /**
         * Builds a quaternion from scalar and vector parts.
         *
         * @param scalar Scalar part of the quaternion.
         * @param v Components of the vector part of the quaternion.
         * @return a quaternion instance.
         *
         * @throws IllegalArgumentException if the array length is not 3.
         */
        fun of(
            scalar: Double,
            v: DoubleArray
        ): Quaternion {
            if (v.size != VECTOR_DIMENSIONS) {
                throw IllegalArgumentException("Size of array must be 3")
            }
            return of(scalar, v[0], v[1], v[2])
        }

        /**
         * Builds a pure quaternion from a vector (assuming that the scalar
         * part is zero).
         *
         * @param v Components of the vector part of the pure quaternion.
         * @return a quaternion instance.
         */
        fun of(v: DoubleArray): Quaternion {
            return of(0.0, v)
        }

        /**
         * Returns the Hamilton product of two quaternions.
         *
         * @param q1 First quaternion.
         * @param q2 Second quaternion.
         * @return the product `q1` and `q2`, in that order.
         */
        fun multiply(
            q1: Quaternion,
            q2: Quaternion
        ): Quaternion {
            // Components of the first quaternion.
            val q1a = q1.w
            val q1b = q1.x
            val q1c = q1.y
            val q1d = q1.z

            // Components of the second quaternion.
            val q2a = q2.w
            val q2b = q2.x
            val q2c = q2.y
            val q2d = q2.z

            // Components of the product.
            val w = q1a * q2a - q1b * q2b - q1c * q2c - q1d * q2d
            val x = q1a * q2b + q1b * q2a + q1c * q2d - q1d * q2c
            val y = q1a * q2c - q1b * q2d + q1c * q2a + q1d * q2b
            val z = q1a * q2d + q1b * q2c - q1c * q2b + q1d * q2a
            return of(w, x, y, z)
        }

        /**
         * Computes the sum of two quaternions.
         *
         * @param q1 Quaternion.
         * @param q2 Quaternion.
         * @return the sum of `q1` and `q2`.
         */
        fun add(
            q1: Quaternion,
            q2: Quaternion
        ): Quaternion {
            return of(
                q1.w + q2.w,
                q1.x + q2.x,
                q1.y + q2.y,
                q1.z + q2.z
            )
        }

        /**
         * Subtracts two quaternions.
         *
         * @param q1 First Quaternion.
         * @param q2 Second quaternion.
         * @return the difference between `q1` and `q2`.
         */
        fun subtract(
            q1: Quaternion,
            q2: Quaternion
        ): Quaternion {
            return of(
                q1.w - q2.w,
                q1.x - q2.x,
                q1.y - q2.y,
                q1.z - q2.z
            )
        }

        /**
         * Computes the dot-product of two quaternions.
         *
         * @param q1 Quaternion.
         * @param q2 Quaternion.
         * @return the dot product of `q1` and `q2`.
         */
        fun dot(
            q1: Quaternion,
            q2: Quaternion
        ): Double {
            return q1.w * q2.w + q1.x * q2.x + q1.y * q2.y + q1.z * q2.z
        }

        /**
         * Parses a string that would be produced by [.toString]
         * and instantiates the corresponding object.
         *
         * @param s String representation.
         * @return an instance.
         * @throws NumberFormatException if the string does not conform
         * to the specification.
         */
        fun parse(s: String?): Quaternion {
            val startBracket = s!!.indexOf(FORMAT_START!!)
            if (startBracket != 0) {
                throw QuaternionParsingException("Expected start string: $FORMAT_START")
            }
            val len = s.length
            val endBracket = s.indexOf(FORMAT_END!!)
            if (endBracket != len - 1) {
                throw QuaternionParsingException("Expected end string: $FORMAT_END")
            }
            val elements: Array<String?> =
                s.substring(1, s.length - 1).split(FORMAT_SEP!!).toTypedArray()
            if (elements.size != NUMBER_OF_PARTS) {
                throw QuaternionParsingException(
                    "Incorrect number of parts: Expected 4 but was " +
                            elements.size +
                            " (separator is '" + FORMAT_SEP + "')"
                )
            }
            val a: Double
            a = try {
                elements[0]!!.toDouble()
            } catch (ex: NumberFormatException) {
                throw QuaternionParsingException("Could not parse scalar part" + elements[0])
            }
            val b: Double
            b = try {
                elements[1]!!.toDouble()
            } catch (ex: NumberFormatException) {
                throw QuaternionParsingException("Could not parse i part" + elements[1])
            }
            val c: Double
            c = try {
                elements[2]!!.toDouble()
            } catch (ex: NumberFormatException) {
                throw QuaternionParsingException("Could not parse j part" + elements[2])
            }
            val d: Double
            d = try {
                elements[3]!!.toDouble()
            } catch (ex: NumberFormatException) {
                throw QuaternionParsingException("Could not parse k part" + elements[3])
            }
            return of(a, b, c, d)
        }
    }
}
