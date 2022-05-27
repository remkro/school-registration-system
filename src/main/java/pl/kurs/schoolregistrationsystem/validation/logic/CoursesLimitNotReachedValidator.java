package pl.kurs.schoolregistrationsystem.validation.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;
import pl.kurs.schoolregistrationsystem.validation.annotation.CoursesLimitNotReached;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class CoursesLimitNotReachedValidator implements ConstraintValidator<CoursesLimitNotReached, Long> {

    private final StudentRepository studentRepository;

    @Value("${student.max-courses}")
    private int maximumCourses;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        int studentCourses = studentRepository.getByIdFetchCourses(id).getCourses().size();
        return studentCourses < maximumCourses;
    }

}
