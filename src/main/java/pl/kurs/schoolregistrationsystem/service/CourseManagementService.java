package pl.kurs.schoolregistrationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.schoolregistrationsystem.command.EditCourseCommand;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseManagementService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public Course add(Course course) {
        if (course == null)
            throw new NoEntityException("NO_ENTITY_TO_ADD");
        if (course.getId() != null)
            throw new WrongIdException("ID_MUST_BE_NULL");
        return courseRepository.saveAndFlush(course);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        if (courseRepository.getById(id).getStudents().size() != 0)
            throw new CourseHasStudentsException("COURSE_HAS_STUDENTS_THUS_CANNOT_BE_DELETED");
        try {
            courseRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoEntityException("NO_ENTITY_FOUND");
        }
    }

//    @Transactional
//    public Course edit(Course course) {
//        if (course == null)
//            throw new NoEntityException("NO_ENTITY_FOUND");
//        if (course.getId() == null)
//            throw new WrongIdException("ID_CANNOT_BE_NULL");
//        return courseRepository.saveAndFlush(course);
//    }

    @Transactional
    public Course edit(EditCourseCommand editCourseCommand) {
        if (!courseRepository.existsById(editCourseCommand.getId()))
            throw new NoEntityException("NO_ENTITY_FOUND");
        Course course = courseRepository.getById(editCourseCommand.getId());
        course.setName(editCourseCommand.getName());
        course.setAgeLimit(editCourseCommand.getAgeLimit());
        return courseRepository.saveAndFlush(course);
    }

    @Transactional(readOnly = true)
    public Course get(Long id) {
        return courseRepository
                .findByIdFetchStudents(
                        Optional.ofNullable(id).orElseThrow(() -> new WrongIdException("ID_CANNOT_BE_NULL"))
                )
                .orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND"));
    }

    @Transactional(readOnly = true)
    public Page<Course> showAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
//        return courseRepository.findAllFetchStudents(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Course> showAll(Long studentId, Pageable pageable) {
//        return courseRepository.findAll(specification, pageable);
        return courseRepository.findAll(getSpecification(studentId), pageable);
//        return courseRepository.findAllFetchStudents(pageable);
    }

    private Specification<Course> getSpecification(Long studentId) {
        return (root, query, criteriaBuilder) -> {
            if (studentId != null) {
                query.distinct(true);
                Subquery<Student> subquery = query.subquery(Student.class);
                Root<Student> student = subquery.from(Student.class);
                Expression<Collection<Course>> studentCourses = student.get("courses");
                subquery.select(student);
                subquery.where(criteriaBuilder.equal(student.get("id"), studentId),
                        criteriaBuilder.isMember(root, studentCourses));
                return criteriaBuilder.exists(subquery);
            }
            return null;
        };
    }

    @Transactional
    public Student addStudentToCourse(EnrollStudentToCourseCommand enroll) {
        Student student = studentRepository.findByIdFetchCourses(
                enroll.getStudentId()).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        Course course = courseRepository.findByIdFetchStudents(
                enroll.getCourseId()).orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND")
        );
        student.addCourse(course);
        return studentRepository.saveAndFlush(student);
    }

    public Student removeStudentFromCourse(long studentId, long courseId) {
        Student student = studentRepository.findByIdFetchCourses(studentId).orElseThrow(
                () -> new NoEntityException("NO_ENTITY_FOUND")
        );
        Course course = courseRepository.findByIdFetchStudents(courseId).orElseThrow(
                () -> new NoEntityException("NO_ENTITY_FOUND")
        );
        student.removeCourse(course);
        return studentRepository.saveAndFlush(student);
    }
}
