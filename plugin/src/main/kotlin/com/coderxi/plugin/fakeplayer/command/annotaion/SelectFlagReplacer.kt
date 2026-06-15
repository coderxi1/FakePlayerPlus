package com.coderxi.plugin.fakeplayer.command.annotaion

import revxrsal.commands.annotation.Flag
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.dynamic.AnnotationReplacer
import revxrsal.commands.annotation.dynamic.Annotations
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Proxy

class SelectFlagReplacer: AnnotationReplacer<SelectFlag> {
    override fun replaceAnnotation(
        element: AnnotatedElement,
        annotation: SelectFlag
    ): Collection<Annotation?> = listOf(
        Proxy.newProxyInstance(Flag::class.java.classLoader,arrayOf(Flag::class.java)) { _, method, _ ->
            when (method.name) {
                "value" -> "select"
                "shorthand" -> 's'
                "annotationType" -> Flag::class.java
                else -> method.defaultValue
            }
        } as Flag
    )
}