package com.coderxi.plugin.fakeplayer.command.annotaion

import revxrsal.commands.annotation.Default
import revxrsal.commands.annotation.Flag
import revxrsal.commands.annotation.dynamic.AnnotationReplacer
import revxrsal.commands.annotation.dynamic.Annotations
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Proxy

class SelectReplacer: AnnotationReplacer<Select> {
    override fun replaceAnnotation(element: AnnotatedElement, annotation: Select): Collection<Annotation?> = listOf(
        Annotations.create(Default::class.java, "value",""),
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