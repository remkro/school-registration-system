package pl.kurs.schoolregistrationsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseDto {

    private Long id;

    private String name;

    private Integer ageLimit;

}
