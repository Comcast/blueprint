/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xfinity.blueprint_compiler

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.tools.Diagnostic.Kind.*
import javax.annotation.processing.Messager as JavaxMessager

internal class Messager {

    private lateinit var messager: JavaxMessager

    fun init(processingEnvironment: ProcessingEnvironment) {
        messager = processingEnvironment.messager
    }

    fun note(e: Element, msg: String, vararg args: Any) {
        checkInitialized()
        messager.printMessage(NOTE, String.format(msg, *args), e)
    }

    fun warn(e: Element, msg: String, vararg args: Any) {
        checkInitialized()
        messager.printMessage(WARNING, String.format(msg, *args), e)
    }

    fun error(e: Element, msg: String, vararg args: Any) {
        checkInitialized()
        messager.printMessage(ERROR, msg.format(*args), e)
    }

    private fun checkInitialized() {
        if (this::messager.isInitialized) {
            throw IllegalStateException("Messager not ready. Have you called init()?")
        }
    }
}
