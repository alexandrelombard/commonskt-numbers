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
package org.apache.commonskt.numbers.field

import org.apache.commonskt.numbers.core.NativeOperators

/**
 * Boiler-plate code for concrete implementations of [Field].
 *
 * @param <T> Type of the field elements.</T>
 */
abstract class AbstractField<T : NativeOperators<T>> : Field<T> {
    /** {@inheritDoc}  */
    override fun add(a: T, b: T): T {
        return a.add(b)
    }

    /** {@inheritDoc}  */
    override fun subtract(a: T, b: T): T {
        return a.subtract(b)
    }

    /** {@inheritDoc}  */
    override fun negate(a: T): T {
        return a.negate()
    }

    /** {@inheritDoc}  */
    override fun multiply(n: Int, a: T): T {
        return a.multiply(n)
    }

    /** {@inheritDoc}  */
    override fun multiply(a: T, b: T): T {
        return a.multiply(b)
    }

    /** {@inheritDoc}  */
    override fun divide(a: T, b: T): T {
        return a.divide(b)
    }

    /** {@inheritDoc}  */
    override fun reciprocal(a: T): T {
        return a.reciprocal()
    }
}
