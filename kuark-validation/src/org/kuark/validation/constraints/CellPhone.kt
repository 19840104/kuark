package org.kuark.validation.constraints

import org.kuark.validation.constraints.validator.CellPhoneValidator
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

/**
 * Create by (admin) on 2015/1/22.
 */
@Documented
@Constraint(validatedBy = [CellPhoneValidator::class])
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(RetentionPolicy.RUNTIME)
annotation class CellPhone(
    val message: String = "{org.kuark.validation.constraints.CellPhone.message}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
) {
    @Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
    )
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    annotation class List(vararg val value: CellPhone)
}