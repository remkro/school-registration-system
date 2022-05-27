package pl.kurs.schoolregistrationsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentManagementService {

    private final StudentRepository studentRepository;

    @Transactional
    public Student add(Student entity) {
        if (entity == null)
            throw new NoEntityException("NO_ENTITY_TO_ADD");
        if (entity.getId() != null)
            throw new WrongIdException("ID_MUST_BE_NULL");
        return studentRepository.saveAndFlush(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (id == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        if (studentRepository.getById(id).getCourses().size() != 0)
            throw new CourseHasStudentsException("STUDENT_HAS_COURSES_THUS_CANNOT_BE_DELETED");
        try {
            studentRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoEntityException("NO_ENTITY_FOUND");
        }
    }

    @Transactional
    public Student edit(Student entity) {
        if (entity == null)
            throw new NoEntityException("NO_ENTITY_FOUND");
        if (entity.getId() == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        return studentRepository.saveAndFlush(entity);
    }

    @Transactional(readOnly = true)
    public Student show(Long id) {
        return studentRepository
                .findByIdFetchCourses(Optional.ofNullable(id).orElseThrow(() -> new WrongIdException("ID_CANNOT_BE_NULL")))
                .orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND"));
    }

    @Transactional(readOnly = true)
    public List<Student> showAll(Pageable pageable) {
        return studentRepository.findAllFetchCourses(pageable);
    }

}
