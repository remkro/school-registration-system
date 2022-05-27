package pl.kurs.schoolregistrationsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String pesel;

    private String email;

}
