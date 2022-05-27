package pl.kurs.schoolregistrationsystem.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.schoolregistrationsystem.validation.annotation.Pesel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddStudentCommand {

    @NotBlank(message = "FIRST_NAME_CANNOT_BE_EMPTY")
    private String firstName;

    @NotBlank(message = "LAST_NAME_CANNOT_BE_EMPTY")
    private String lastName;

    @NotBlank(message = "PESEL_CANNOT_BE_EMPTY")
    @Pesel(message = "PESEL_INCORRECT")
    private String pesel;

    @NotBlank(message = "EMAIL_CANNOT_BE_EMPTY")
    @Pattern(regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", message = "EMAIL_INCORRECT")
    private String email;

}
