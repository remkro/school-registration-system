package pl.kurs.schoolregistrationsystem.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddCourseCommand {

    @NotBlank(message = "COURSE_NAME_CANNOT_BE_EMPTY")
    private String name;

    @Positive(message = "AGE_LIMIT_MUST_BE_POSITIVE")
    @NotNull(message = "AGE_LIMIT_CANNOT_BE_NULL")
    private Integer ageLimit;

}
