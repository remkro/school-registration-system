package pl.kurs.schoolregistrationsystem.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.schoolregistrationsystem.model.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = ?1")
    Optional<Course> findByIdFetchStudents(Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = ?1")
    Course getByIdFetchStudents(Long id);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students")
    List<Course> findAllFetchStudents(Pageable pageable);

}
