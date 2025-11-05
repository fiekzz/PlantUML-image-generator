package com.fiekzz.com.puml.utils.validator

import com.fiekzz.com.puml.utils.plantuml.UmlOutput
import javax.validation.Constraint
import javax.validation.ConstraintValidatorContext


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UMLTextValidator::class])
annotation class ValidUMLText()

class UMLTextValidator: javax.validation.ConstraintValidator<ValidUMLText, String> {

    override fun initialize(constraintAnnotation: ValidUMLText?) {
        super.initialize(constraintAnnotation)
    }

    override fun isValid(text: String?, context: ConstraintValidatorContext?): Boolean {
        if (text == null || text.isEmpty()) return false

        val outputType = UmlOutput.tryFindOutputByName(text)

        if (outputType != null) {
            return true
        }

        return false
    }
}