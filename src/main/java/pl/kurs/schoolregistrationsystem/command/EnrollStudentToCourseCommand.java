package pl.kurs.schoolregistrationsystem.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.schoolregistrationsystem.validation.annotation.CourseExists;
import pl.kurs.schoolregistrationsystem.validation.annotation.CoursesLimitNotReached;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentExists;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentNotEnrolled;
import pl.kurs.schoolregistrationsystem.validation.annotation.StudentsLimitNotReached;
import pl.kurs.schoolregistrationsystem.validation.order.FirstCheck;
import pl.kurs.schoolregistrationsystem.validation.order.SecondCheck;
import pl.kurs.schoolregistrationsystem.validation.order.ThirdCheck;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@GroupSequence({FirstCheck.class, SecondCheck.class, ThirdCheck.class, EnrollStudentToCourseCommand.class})
@StudentNotEnrolled(message = "STUDENT_ALREADY_ENROLLED_IN_THE_COURSE")
public class EnrollStudentToCourseCommand {

    @NotNull(message = "STUDENT_ID_CANNOT_BE_NULL", groups = FirstCheck.class)
    @StudentExists(message = "STUDENT_DOES_NOT_EXIST", groups = SecondCheck.class)
    @CoursesLimitNotReached(message = "STUDENT_IS_ENROLLED_TO_MAXIMUM_NUMBER_OF_COURSES", groups = ThirdCheck.class)
    private Long studentId;

    @NotNull(message = "COURSE_ID_CANNOT_BE_NULL", groups = FirstCheck.class)
    @CourseExists(message = "COURSE_DOES_NOT_EXIST", groups = SecondCheck.class)
    @StudentsLimitNotReached(message = "COURSE_HAS_MAXIMUM_NUMBER_OF_STUDENTS", groups = ThirdCheck.class)
    private Long courseId;

}
