package com.fiekzz.com.puml.utils.validator

import javax.validation.ConstraintValidatorContext


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidUMLText()

class UMLTextValidator: javax.validation.ConstraintValidator<ValidUMLText, String> {

    override fun initialize(constraintAnnotation: ValidUMLText?) {
        super.initialize(constraintAnnotation)
    }

    override fun isValid(text: String?, context: ConstraintValidatorContext?): Boolean {
        return !(text == null || text.isEmpty())
    }
}