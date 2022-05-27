package pl.kurs.schoolregistrationsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.model.dto.StatusDto;
import pl.kurs.schoolregistrationsystem.service.RegistrationManagementService;

import javax.validation.Valid;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationManagementService registrationManagementService;

    @PostMapping
    public ResponseEntity<StatusDto> enroll(@RequestBody @Valid EnrollStudentToCourseCommand enrollStudentToCourseCommand) {
        registrationManagementService.addStudentToCourse(enrollStudentToCourseCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StatusDto("Student enrolled to course!"));
    }

    @PutMapping("/remove/student/{studentId}/course/{courseId}")
    public ResponseEntity<StatusDto> cancel(@PathVariable("studentId") long studentId, @PathVariable("courseId") long courseId) {
        registrationManagementService.removeStudentFromCourse(studentId, courseId);
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto("Student removed from course!"));
    }

}
