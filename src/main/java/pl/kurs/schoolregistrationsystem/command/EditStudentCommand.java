package pl.kurs.schoolregistrationsystem.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditStudentCommand {

    @NotBlank(message = "ID_CANNOT_BE_EMPTY")
    private Long id;

    @NotBlank(message = "FIRST_NAME_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "LAST_NAME_CANNOT_BE_EMPTY")
    private String lastName;

    @NotBlank(message = "PESEL_CANNOT_BE_EMPTY")
    private String pesel;

    @NotBlank(message = "EMAIL_CANNOT_BE_EMPTY")
    private String email;

}
