package pl.kurs.schoolregistrationsystem.validation.logic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentNotEnrolled;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
@RequiredArgsConstructor
public class StudentNotEnrolledValidator implements ConstraintValidator<StudentNotEnrolled, Object> {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        if(!(object instanceof EnrollStudentToCourseCommand))
            return true;
        EnrollStudentToCourseCommand command = (EnrollStudentToCourseCommand) object;
        Student student = studentRepository.getByIdFetchCourses(command.getStudentId());
        Course course = courseRepository.getByIdFetchStudents(command.getCourseId());
        return !course.getStudents().contains(student);
    }
}
