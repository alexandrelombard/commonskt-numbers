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
package org.apache.commonskt.numbers.fraction

/**
 * Package private exception class with constants for frequently used messages.
 */
internal class FractionException : ArithmeticException {
    /**
     * Create an exception
     *
     * @param message  the exception message
     */
    constructor(message: String) : super(message)

    /**
     * Create an exception with the specified message.
     *
     * @param message  the exception message
     */
    constructor(message: String?) : super(message) {}

    companion object {
        /** Error message for overflow during conversion.  */
        fun ERROR_CONVERSION_OVERFLOW(s: Double, p: Long, q: Long) = "Overflow trying to convert %s to fraction (%d/%d)"

        /** Error message when iterative conversion fails.  */
        fun ERROR_CONVERSION(s: Double, i: Int) = "Unable to convert %s to fraction after %d iterations"

        /** Error message for zero-valued denominator.  */
        const val ERROR_ZERO_DENOMINATOR = "Denominator must be different from 0"

        /** Error message for divide by zero.  */
        const val ERROR_DIVIDE_BY_ZERO = "The value to divide by must not be zero"
    }
}