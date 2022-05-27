package pl.kurs.schoolregistrationsystem.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.schoolregistrationsystem.command.AddStudentCommand;
import pl.kurs.schoolregistrationsystem.command.EditStudentCommand;
import pl.kurs.schoolregistrationsystem.model.dto.StatusDto;
import pl.kurs.schoolregistrationsystem.model.dto.StudentDto;
import pl.kurs.schoolregistrationsystem.model.dto.StudentWithCoursesDto;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.service.StudentManagementService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentManagementService studentManagementService;
    private final ModelMapper mapper;

    @PostMapping
    public ResponseEntity<StudentDto> add(@RequestBody @Valid AddStudentCommand addStudentCommand) {
        Student student = new Student(
                addStudentCommand.getFirstName(),
                addStudentCommand.getLastName(),
                addStudentCommand.getPesel(),
                addStudentCommand.getEmail()
        );
        student = studentManagementService.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(student, StudentDto.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> get(@PathVariable long id) {
        Student student = studentManagementService.show(id);
        return ResponseEntity.ok(mapper.map(student, StudentDto.class));
    }

    @GetMapping
    public ResponseEntity<List<StudentWithCoursesDto>> getAll(@PageableDefault Pageable pageable) {
        List<StudentWithCoursesDto> students = studentManagementService.showAll(pageable)
                .stream()
                .map(s -> mapper.map(s, StudentWithCoursesDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @PutMapping
    public ResponseEntity<StudentDto> edit(@RequestBody EditStudentCommand editStudentCommand) {
        Student student = studentManagementService.show(editStudentCommand.getId());
        student.setFirstName(editStudentCommand.getFirstName());
        student.setLastName(editStudentCommand.getLastName());
        student.setPesel(editStudentCommand.getPesel());
        student.setEmail(editStudentCommand.getEmail());
        student = studentManagementService.edit(student);
        return ResponseEntity.ok(mapper.map(student, StudentDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StatusDto> delete(@PathVariable("id") long id) {
        studentManagementService.delete(id);
        return ResponseEntity.ok(new StatusDto("Student removed!"));
    }

    @GetMapping("/without-courses")
    public ResponseEntity<List<StudentDto>> getAllWithoutCourses(@PageableDefault Pageable pageable) {
        List<StudentDto> students = studentManagementService.showAll(pageable)
                .stream()
                .filter(s -> s.getCourses().size() == 0)
                .map(s -> mapper.map(s, StudentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

    @GetMapping("/enrolled-to-course/{id}")
    public ResponseEntity<List<StudentDto>> getAllEnrolledToSpecificCourse(@PathVariable("id") long id, @PageableDefault Pageable pageable) {
        List<StudentDto> students = studentManagementService.showAll(pageable)
                .stream()
                .filter(s -> s.attendsCourse(id))
                .map(s -> mapper.map(s, StudentDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(students);
    }

}
