package pl.kurs.schoolregistrationsystem.validation.annotation;

import pl.kurs.schoolregistrationsystem.validation.logic.CoursesLimitNotReachedValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CoursesLimitNotReachedValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CoursesLimitNotReached {

        String message() default "Student is already enrolled to maximum number of courses!";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

}
