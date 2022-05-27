package pl.kurs.schoolregistrationsystem.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.stereotype.Service;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

@Service
@RequiredArgsConstructor
class StudentManagementServiceTest {

    private StudentManagementService studentManagementService;

    private final StudentRepository studentRepository = Mockito.mock(StudentRepository.class);

    @BeforeEach
    void init() {
        this.studentManagementService = new StudentManagementService(studentRepository);
    }

    @Test
    void shouldAddStudent() {
        Student studentToAdd = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        Mockito.when((studentRepository.saveAndFlush(any(Student.class)))).then(returnsFirstArg());
        Student addedStudent = studentManagementService.add(studentToAdd);
        assertEquals(studentToAdd.getFirstName(), addedStudent.getFirstName());
        assertEquals(studentToAdd.getLastName(), addedStudent.getLastName());
        assertEquals(studentToAdd.getPesel(), addedStudent.getPesel());
        assertEquals(studentToAdd.getEmail(), addedStudent.getEmail());
    }

    @Test
    void shouldDeleteStudent() {
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        Mockito.when(studentRepository.getById(1L)).thenReturn(student);
        studentManagementService.delete(1L);
        Mockito.verify(studentRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void shouldEditStudent() {
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        Mockito.when((studentRepository.saveAndFlush(any(Student.class)))).then(returnsFirstArg());
        Student editedStudent = studentManagementService.edit(student);
        assertEquals(student.getId(), editedStudent.getId());
        assertEquals(student.getFirstName(), editedStudent.getFirstName());
        assertEquals(student.getLastName(), editedStudent.getLastName());
        assertEquals(student.getPesel(), editedStudent.getPesel());
        assertEquals(student.getEmail(), editedStudent.getEmail());
    }

    @Test
    void shouldShowStudent() {
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.of(student));
        Student showedStudent = studentManagementService.show(1L);
        assertEquals(student.getId(), showedStudent.getId());
        assertEquals(student.getFirstName(), showedStudent.getFirstName());
        assertEquals(student.getLastName(), showedStudent.getLastName());
        assertEquals(student.getPesel(), showedStudent.getPesel());
        assertEquals(student.getEmail(), showedStudent.getEmail());
    }

    @Test
    void shouldFailToAddStudentWhenStudentIsNull() {
        Student studentToAdd = null;
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            studentManagementService.add(studentToAdd);
        });
        Assertions.assertEquals("NO_ENTITY_TO_ADD", exception.getMessage());
    }

    @Test
    void shouldFailToAddStudentWhenStudentIdIsNotNull() {
        Student studentToAdd = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        studentToAdd.setId(1L);
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            studentManagementService.add(studentToAdd);
        });
        Assertions.assertEquals("ID_MUST_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToDeleteStudentWhenStudentIdIsNull() {
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            studentManagementService.delete(null);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToDeleteStudentWhenStudentHasCourses() {
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        Course course = new Course("testowy kurs", 1);
        student.addCourse(course);
        Mockito.when(studentRepository.getById(1L)).thenReturn(student);
        CourseHasStudentsException exception = assertThrows(CourseHasStudentsException.class, () -> {
            studentManagementService.delete(1L);
        });
        Assertions.assertEquals("STUDENT_HAS_COURSES_THUS_CANNOT_BE_DELETED", exception.getMessage());
    }

    @Test
    void shouldFailToEditStudentWhenStudentIsNull() {
        Student studentToEdit = null;
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            studentManagementService.edit(studentToEdit);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

    @Test
    void shouldFailToEditStudentWhenStudentIdIsNull() {
        Student studentToEdit = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            studentManagementService.edit(studentToEdit);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToShowStudentWhenIdIsNull() {
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            studentManagementService.show(null);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToShowStudentWhenThereIsNoStudent() {
        Mockito.when(studentRepository.findByIdFetchCourses(1L)).thenReturn(Optional.empty());
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            studentManagementService.show(1L);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

}