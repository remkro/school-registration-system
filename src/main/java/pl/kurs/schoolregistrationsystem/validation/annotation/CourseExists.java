package pl.kurs.schoolregistrationsystem.validation.annotation;

import pl.kurs.schoolregistrationsystem.validation.logic.CourseExistsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = CourseExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CourseExists {

    String message() default "Course does not exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
