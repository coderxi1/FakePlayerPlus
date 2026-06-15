package com.coderxi.plugin.fakeplayer.utils

import com.google.common.reflect.ClassPath

object ReflectUtil {

    fun loadPackage(packageName: String): List<Class<*>> {
        val loader = Thread.currentThread().contextClassLoader
        val classPath = ClassPath.from(loader)
        return classPath.getTopLevelClasses(packageName).mapNotNull { classInfo -> try { classInfo.load() } catch (_: Throwable) { null }  }
    }

}