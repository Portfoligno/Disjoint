package io.github.portfoligno.json.disjoint.utility

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

@Target(FILE, CLASS, CONSTRUCTOR)
@Retention(BINARY)
annotation class JvmPackagePrivate

const val jvmPackagePrivateWarning = "Minimal use from the same package only"
