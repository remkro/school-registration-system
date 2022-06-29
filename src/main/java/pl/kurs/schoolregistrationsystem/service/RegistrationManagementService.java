package pl.kurs.schoolregistrationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class RegistrationManagementService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Value("${student.max-courses}")
    private int maximumCourses;

    @Value("${course.max-students}")
    private int maximumStudents;

    @Transactional
    public Student addStudentToCourse(EnrollStudentToCourseCommand enrollStudentToCourseCommand) {
        Student student = studentRepository.findByIdFetchCourses(
                enrollStudentToCourseCommand.getStudentId()).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        Course course = courseRepository.findByIdFetchStudents(
                enrollStudentToCourseCommand.getCourseId()).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        student.addCourse(course);
        return studentRepository.saveAndFlush(student);
    }

    @Transactional
    public Student removeStudentFromCourse(long studentId, long courseId) {
        Student student = studentRepository.findByIdFetchCourses(
                studentId).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        Course course = courseRepository.findByIdFetchStudents(
                courseId).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        student.removeCourse(course);
        return studentRepository.saveAndFlush(student);
    }

}
