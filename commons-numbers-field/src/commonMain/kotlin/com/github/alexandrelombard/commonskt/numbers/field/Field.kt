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
package com.github.alexandrelombard.commonskt.numbers.field

/**
 * Interface representing a [field](http://mathworld.wolfram.com/Field.html).
 *
 * @param <T> Type of the field elements.</T>
 */
interface Field<T> {
    /**
     * @param a Field element.
     * @param b Field element.
     * @return `a + b`.
     */
    fun add(a: T, b: T): T

    /**
     * @param a Field element.
     * @param b Field element.
     * @return `a - b`.
     */
    fun subtract(a: T, b: T): T

    /**
     * @param a Field element.
     * @return `-a`.
     */
    fun negate(a: T): T

    /**
     * @param a Field element.
     * @param n Number of times `a` must be added to itself.
     * @return `n a`.
     */
    fun multiply(n: Int, a: T): T

    /**
     * @param a Field element.
     * @param b Field element.
     * @return `a * b`.
     */
    fun multiply(a: T, b: T): T

    /**
     * @param a Field element.
     * @param b Field element.
     * @return `a * b<sup>-1</sup>`.
     */
    fun divide(a: T, b: T): T

    /**
     * @param a Field element.
     * @return `a<sup>-1</sup>`.
     */
    fun reciprocal(a: T): T

    /**
     * @return the field element `1` such that for all `a`,
     * `1 * a == a`.
     */
    fun one(): T

    /**
     * @return the field element `0` such that for all `a`,
     * `0 + a == a`.
     */
    fun zero(): T
}
