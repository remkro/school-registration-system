package pl.kurs.schoolregistrationsystem.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.kurs.schoolregistrationsystem.model.interfaces.Identificationable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course implements Identificationable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_course")
    private Long id;

    private String name;

    private Integer ageLimit;

    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();

    @Version
    private Integer version;

    public Course() {
    }

    public Course(String name, Integer ageLimit) {
        this.name = name;
        this.ageLimit = ageLimit;
    }

    public boolean hasStudent(Long id) {
        return students.stream().anyMatch(s -> s.getId().equals(id));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id) && Objects.equals(name, course.name) && Objects.equals(ageLimit, course.ageLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, ageLimit);
    }

}
