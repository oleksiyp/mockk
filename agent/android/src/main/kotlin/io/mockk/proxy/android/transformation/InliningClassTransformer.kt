/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mockk.proxy.android.transformation

import io.mockk.proxy.MockKAgentException
import io.mockk.proxy.common.transformation.ClassTransformationSpecMap
import java.util.*

internal class InliningClassTransformer(
    private val specMap: ClassTransformationSpecMap
) {
    val identifier = newId()

    @Suppress("unused") // JNI call
    fun transform(classBeingRedefined: Class<*>?, classfileBuffer: ByteArray) =
        when {
            classBeingRedefined == null ->
                null

            !shouldTransform(classBeingRedefined) ->
                classfileBuffer

            else -> synchronized(lock) {
                try {
                    // FIXME specialize transformation
                    nativeRedefine(identifier, classfileBuffer)
                } catch (ex: Exception) {
                    throw MockKAgentException("Transformation issue", ex)
                }
            }
        }

    fun shouldTransform(classBeingRedefined: Class<*>) =
        specMap[classBeingRedefined]?.shouldDoSomething == true

    private external fun nativeRedefine(identifier: String, original: ByteArray): ByteArray

    companion object {
        private val lock = Any()
        private val rng = Random()
        private fun newId() = Math.abs(rng.nextLong()).toString(16)
    }
}
