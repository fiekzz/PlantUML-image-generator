package com.fiekzz.com.puml.utils.runtimehints

import com.fiekzz.com.puml.utils.cache.UMLCache
import com.fiekzz.com.puml.utils.configurations.PlantUMLRuntimeHints
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar

class AppRunTimeHints: RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        hints.reflection().registerType(
            UMLCache::class.java,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
        )
        hints.reflection().registerType(
            PlantUMLRuntimeHints::class.java,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
        )
    }
}