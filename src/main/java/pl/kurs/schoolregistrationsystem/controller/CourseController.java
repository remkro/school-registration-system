package pl.kurs.schoolregistrationsystem.controller;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.schoolregistrationsystem.command.AddCourseCommand;
import pl.kurs.schoolregistrationsystem.command.EditCourseCommand;
import pl.kurs.schoolregistrationsystem.model.dto.CourseDto;
import pl.kurs.schoolregistrationsystem.model.dto.StatusDto;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.service.CourseManagementService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseManagementService courseManagementService;
    private final ModelMapper mapper;

    public CourseController(CourseManagementService courseManagementService, ModelMapper mapper) {
        this.courseManagementService = courseManagementService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<CourseDto> add(@RequestBody @Valid AddCourseCommand addCourseCommand) {
        Course course = new Course(
                addCourseCommand.getName(),
                addCourseCommand.getAgeLimit()
        );
        course = courseManagementService.add(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(course, CourseDto.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> get(@PathVariable long id) {
        Course course = courseManagementService.show(id);
        return ResponseEntity.ok(mapper.map(course, CourseDto.class));
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAll(@PageableDefault Pageable pageable) {
        List<CourseDto> courses = courseManagementService.showAll(pageable)
                .stream()
                .map(c -> mapper.map(c, CourseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @PutMapping
    public ResponseEntity<CourseDto> edit(@RequestBody EditCourseCommand editCourseCommand) {
        Course course = courseManagementService.show(editCourseCommand.getId());
        course.setName(editCourseCommand.getName());
        course.setAgeLimit(editCourseCommand.getAgeLimit());
        course = courseManagementService.edit(course);
        return ResponseEntity.ok(mapper.map(course, CourseDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StatusDto> delete(@PathVariable("id") long id) {
        courseManagementService.delete(id);
        return ResponseEntity.ok(new StatusDto("Course removed!"));
    }

    @GetMapping("/without-student")
    public ResponseEntity<List<CourseDto>> getAllWithoutStudent(@PageableDefault Pageable pageable) {
        List<CourseDto> courses = courseManagementService.showAll(pageable)
                .stream()
                .filter(c -> c.getStudents().size() == 0)
                .map(c -> mapper.map(c, CourseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/enrolled-student/{id}")
    public ResponseEntity<List<CourseDto>> getAllForEnrolledStudent(@PathVariable("id") long id, @PageableDefault Pageable pageable) {
        List<CourseDto> courses = courseManagementService.showAll(pageable)
                .stream()
                .filter(c -> c.hasStudent(id))
                .map(c -> mapper.map(c, CourseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(courses);
    }

}
