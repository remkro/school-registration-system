package pl.kurs.schoolregistrationsystem.validation.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentsLimitNotReached;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class StudentsLimitNotReachedValidator implements ConstraintValidator<StudentsLimitNotReached, Long> {

    private final CourseRepository courseRepository;

    @Value("${course.max-students}")
    private int maximumStudents;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        int courseStudents = courseRepository.getByIdFetchStudents(id).getStudents().size();
        return courseStudents < maximumStudents;
    }

}
