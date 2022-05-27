package pl.kurs.schoolregistrationsystem.validation.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.validation.annotation.CourseExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class CourseExistsValidator implements ConstraintValidator<CourseExists, Long> {

    private final CourseRepository courseRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return courseRepository.existsById(id);
    }

}
