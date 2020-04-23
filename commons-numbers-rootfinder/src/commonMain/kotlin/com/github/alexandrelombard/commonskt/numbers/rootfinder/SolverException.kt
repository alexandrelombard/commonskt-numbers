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
package com.github.alexandrelombard.commonskt.numbers.rootfinder

/**
 * Package private exception class with constants for frequently used messages.
 */
internal class SolverException
/**
 * Create an exception where the message is constructed by applying
 * the `format()` method from `java.text.MessageFormat`.
 *
 * @param message  the exception message with replaceable parameters
 * @param formatArguments the arguments for formatting the message
 */
    (message: String) : IllegalArgumentException(message) {
    companion object {
        /** Error message for "too large" condition.  */
        fun TOO_LARGE(a: Double, b: Double) = "$a > $b"

        /** Error message for "out of range" condition.  */
        fun OUT_OF_RANGE(n: Double, a: Double, b: Double) = "$n is out of range [$a, $b]"

        /** Error message for "failed bracketing" condition.  */
        fun BRACKETING(a: Double, b: Double, c: Double, d: Double) = "No bracketing: f($a)=$b, f($c)=$d"
    }
}