package pl.kurs.schoolregistrationsystem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

class RegistrationManagementServiceTest {

    private RegistrationManagementService registrationManagementService;

    private final StudentRepository studentRepository = Mockito.mock(StudentRepository.class);

    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);

    @BeforeEach
    void init() {
        this.registrationManagementService = new RegistrationManagementService(studentRepository, courseRepository);
    }

    @Test
    void shouldAddStudentToCourse() {
        Course course = new Course("testowy kurs", 100);
        course.setId(1L);
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.of(course));
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.of(student));
        EnrollStudentToCourseCommand command = new EnrollStudentToCourseCommand(1L, 1L);
        Mockito.when((studentRepository.saveAndFlush(any(Student.class)))).then(returnsFirstArg());
        Student enrolledStudent = registrationManagementService.addStudentToCourse(command);
        assertEquals(1, student.getCourses().size());
        assertEquals(student.getFirstName(), enrolledStudent.getFirstName());
        assertEquals(student.getLastName(), enrolledStudent.getLastName());
        assertEquals(student.getPesel(), enrolledStudent.getPesel());
        assertEquals(student.getEmail(), enrolledStudent.getEmail());
    }

    @Test
    void shouldRemoveStudentFromCourse() {
        Course course = new Course("testowy kurs", 100);
        course.setId(1L);
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.of(course));
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        student.addCourse(course);
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.of(student));
        Mockito.when((studentRepository.saveAndFlush(any(Student.class)))).then(returnsFirstArg());
        Student studentRemovedFromCourse = registrationManagementService.removeStudentFromCourse(1L, 1L);
        assertEquals(0, student.getCourses().size());
        assertEquals(student.getFirstName(), studentRemovedFromCourse.getFirstName());
        assertEquals(student.getLastName(), studentRemovedFromCourse.getLastName());
        assertEquals(student.getPesel(), studentRemovedFromCourse.getPesel());
        assertEquals(student.getEmail(), studentRemovedFromCourse.getEmail());
    }

    @Test
    void shouldFailToAddStudentToCourseWhenThereIsNoStudent() {
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.empty());
        EnrollStudentToCourseCommand command = new EnrollStudentToCourseCommand(1L, 1L);
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            registrationManagementService.addStudentToCourse(command);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

    @Test
    void shouldFailToAddStudentToCourseWhenThereIsNoCourse() {
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.empty());
        EnrollStudentToCourseCommand command = new EnrollStudentToCourseCommand(1L, 1L);
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            registrationManagementService.addStudentToCourse(command);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

    @Test
    void shouldFailToRemoveStudentFromCourseWhenThereIsNoStudent() {
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.empty());
        EnrollStudentToCourseCommand command = new EnrollStudentToCourseCommand(1L, 1L);
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            registrationManagementService.addStudentToCourse(command);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

    @Test
    void shouldFailToRemoveStudentFromCourseWhenThereIsNoCourse() {
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.empty());
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            registrationManagementService.removeStudentFromCourse(1L, 1L);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

}