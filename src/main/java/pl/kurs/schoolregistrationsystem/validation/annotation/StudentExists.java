package pl.kurs.schoolregistrationsystem.validation.annotation;

import pl.kurs.schoolregistrationsystem.validation.logic.StudentExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = StudentExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StudentExists {

    String message() default "Student does not exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

