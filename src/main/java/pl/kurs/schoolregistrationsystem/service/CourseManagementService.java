package pl.kurs.schoolregistrationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseManagementService{

    private final CourseRepository courseRepository;

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

    @Transactional
    public Course edit(Course course) {
        if (course == null)
            throw new NoEntityException("NO_ENTITY_FOUND");
        if (course.getId() == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        return courseRepository.saveAndFlush(course);
    }

    @Transactional(readOnly = true)
    public Course show(Long id) {
        return courseRepository
                .findByIdFetchStudents(Optional.ofNullable(id).orElseThrow(() -> new WrongIdException("ID_CANNOT_BE_NULL")))
                .orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<Course> showAll(Pageable pageable) {
        return courseRepository.findAllFetchStudents(pageable);
    }

}
