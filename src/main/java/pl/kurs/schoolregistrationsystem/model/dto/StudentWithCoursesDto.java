package pl.kurs.schoolregistrationsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentWithCoursesDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String pesel;

    private String email;

    private List<String> courses;

    @Override
    public String toString() {
        return "StudentWithCoursesDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pesel='" + pesel + '\'' +
                ", email='" + email + '\'' +
                ", courses=" + courses +
                '}';
    }

}
