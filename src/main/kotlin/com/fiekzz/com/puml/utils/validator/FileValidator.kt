package com.fiekzz.com.puml.utils.validator

import com.fiekzz.com.puml.model.appmessages.AppMessages
import org.springframework.web.multipart.MultipartFile
import javax.validation.Constraint
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FileValidator::class])
annotation class ValidFile(
    val message: String = "Invalid file",
    val maxSize: Long = 1024 * 1024 * 1024,
    val allowedTypes: Array<String> = ["text/plantuml", "application/x-plantuml", "text/plain"],
    val payload: Array<kotlin.reflect.KClass<out Any>> = []
)

class FileValidator: javax.validation.ConstraintValidator<ValidFile, MultipartFile?> {
    private var maxSize: Long = 0
    private var allowedTypes: Array<String> = emptyArray()

    override fun initialize(constraintAnnotation: ValidFile) {
        super.initialize(constraintAnnotation)
        this.maxSize = constraintAnnotation.maxSize
        this.allowedTypes = constraintAnnotation.allowedTypes
    }

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        if (file == null || file.isEmpty) return false

        if (file.size > maxSize) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("${AppMessages.File.exceededFileSize} ${maxSize / 1024 / 1024}MB")
                .addConstraintViolation()
            return false
        }

        if (file.contentType !in allowedTypes) {
            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate("${AppMessages.File.errorFileType} ${allowedTypes.joinToString()}")
                .addConstraintViolation()
            return false
        }
        return true
    }
}