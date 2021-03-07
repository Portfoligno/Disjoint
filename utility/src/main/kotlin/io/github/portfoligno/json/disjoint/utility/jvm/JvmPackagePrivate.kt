package io.github.portfoligno.json.disjoint.utility.jvm

import kotlin.annotation.AnnotationRetention.BINARY
import kotlin.annotation.AnnotationTarget.*

@Target(FILE, CLASS)
@Retention(BINARY)
annotation class JvmPackagePrivate
