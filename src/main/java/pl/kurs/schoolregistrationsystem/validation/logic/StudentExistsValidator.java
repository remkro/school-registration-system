package pl.kurs.schoolregistrationsystem.validation.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentExists;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class StudentExistsValidator implements ConstraintValidator<StudentExists, Long> {

    private final StudentRepository studentRepository;

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return studentRepository.existsById(id);
    }

}
