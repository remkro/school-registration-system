package pl.kurs.schoolregistrationsystem.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

class CourseManagementServiceTest {

    private CourseManagementService courseManagementService;

    private final CourseRepository courseRepository = Mockito.mock(CourseRepository.class);

    @BeforeEach
    void init() {
        this.courseManagementService = new CourseManagementService(courseRepository);
    }

    @Test
    void shouldAddCourse() {
        Course courseToAdd = new Course("testowy kurs", 100);
        Mockito.when((courseRepository.saveAndFlush(any(Course.class)))).then(returnsFirstArg());
        Course addedCourse = courseManagementService.add(courseToAdd);
        assertEquals(courseToAdd.getName(), addedCourse.getName());
        assertEquals(courseToAdd.getAgeLimit(), addedCourse.getAgeLimit());
    }

    @Test
    void shouldDeleteCourse() {
        Course course = new Course("testowy kurs", 100);
        Mockito.when(courseRepository.getById(1L)).thenReturn(course);
        courseManagementService.delete(1L);
        Mockito.verify(courseRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void shouldEditCourse() {
        Course course = new Course("testowy kurs", 100);
        course.setId(1L);
        Mockito.when((courseRepository.saveAndFlush(any(Course.class)))).then(returnsFirstArg());
        Course editedCourse = courseManagementService.edit(course);
        assertEquals(course.getId(), editedCourse.getId());
        assertEquals(course.getName(), editedCourse.getName());
        assertEquals(course.getAgeLimit(), editedCourse.getAgeLimit());
    }

    @Test
    void shouldShowCourse() {
        Course course = new Course("testowy kurs", 100);
        course.setId(1L);
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.of(course));
        Course showedCourse = courseManagementService.show(1L);
        assertEquals(course.getId(), showedCourse.getId());
        assertEquals(course.getName(), showedCourse.getName());
        assertEquals(course.getAgeLimit(), showedCourse.getAgeLimit());
    }

    @Test
    void shouldFailToAddCourseWhenCourseIsNull() {
        Course courseToAdd = null;
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            courseManagementService.add(courseToAdd);
        });
        Assertions.assertEquals("NO_ENTITY_TO_ADD", exception.getMessage());
    }

    @Test
    void shouldFailToAddCourseWhenCourseIdIsNotNull() {
        Course courseToAdd = new Course("testowy kurs", 100);
        courseToAdd.setId(1L);
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            courseManagementService.add(courseToAdd);
        });
        Assertions.assertEquals("ID_MUST_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToDeleteCourseWhenCourseIdIsNull() {
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            courseManagementService.delete(null);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToDeleteCourseWhenCourseHasCourses() {
        Student student = new Student("Andrzej", "Kolikowski", "41091658907", "endrju@email.com");
        student.setId(1L);
        Course course = new Course("testowy kurs", 1);
        student.addCourse(course);
        Mockito.when(courseRepository.getById(1L)).thenReturn(course);
        CourseHasStudentsException exception = assertThrows(CourseHasStudentsException.class, () -> {
            courseManagementService.delete(1L);
        });
        Assertions.assertEquals("COURSE_HAS_STUDENTS_THUS_CANNOT_BE_DELETED", exception.getMessage());
    }

    @Test
    void shouldFailToEditCourseWhenCourseIsNull() {
        Course courseToEdit = null;
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            courseManagementService.edit(courseToEdit);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

    @Test
    void shouldFailToEditCourseWhenCourseIdIsNull() {
        Course courseToEdit = new Course("testowy kurs", 1);
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            courseManagementService.edit(courseToEdit);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToShowCourseWhenIdIsNull() {
        WrongIdException exception = assertThrows(WrongIdException.class, () -> {
            courseManagementService.show(null);
        });
        Assertions.assertEquals("ID_CANNOT_BE_NULL", exception.getMessage());
    }

    @Test
    void shouldFailToShowCourseWhenThereIsCourse() {
        Mockito.when(courseRepository.findByIdFetchStudents(1L)).thenReturn(Optional.empty());
        NoEntityException exception = assertThrows(NoEntityException.class, () -> {
            courseManagementService.show(1L);
        });
        Assertions.assertEquals("NO_ENTITY_FOUND", exception.getMessage());
    }

}