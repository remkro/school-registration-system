package pl.kurs.schoolregistrationsystem.controller;

import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.schoolregistrationsystem.command.AddCourseCommand;
import pl.kurs.schoolregistrationsystem.command.EditCourseCommand;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.command.RemoveStudentFromCourseCommand;
import pl.kurs.schoolregistrationsystem.controller.specification.CourseSpecificationsBuilder;
import pl.kurs.schoolregistrationsystem.controller.specification.SearchOperation;
import pl.kurs.schoolregistrationsystem.model.dto.CourseDto;
import pl.kurs.schoolregistrationsystem.model.dto.StatusDto;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;
import pl.kurs.schoolregistrationsystem.service.CourseManagementService;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseManagementService courseManagementService;
    private final ModelMapper mapper;

    /*

    wstrzykujemy przez konstruktor ponieważ:
    - jest to łatwiejsze do testowania (mozemy w wygodny sposob wstrzyknąć zależności do konstruktora w testach)
    - jest bardziej clean (mniej linijek)

     */

    @PostConstruct
    public void init() {
        courseManagementService.add(new Course("kurs gotowania", 1));
        courseManagementService.add(new Course("kurs tańcowania", 100));
    }

//    public CourseController(CourseManagementService courseManagementService, ModelMapper mapper) {
//        this.courseManagementService = courseManagementService;
//        this.mapper = mapper;
//    }

//    @PostMapping("/enroll")
//    public ResponseEntity<StatusDto> enroll(@RequestBody @Valid EnrollStudentToCourseCommand enrollStudentToCourseCommand) {
//        registrationManagementService.addStudentToCourse(enrollStudentToCourseCommand);
//        return ResponseEntity.status(HttpStatus.CREATED).body(new StatusDto("Student enrolled to course!"));
//    }
//
//    @PutMapping("/remove")
//    public ResponseEntity<StatusDto> cancel(@RequestBody .. //@PathVariable("studentId") long studentId, @PathVariable("courseId") long courseId) {
//        registrationManagementService.removeStudentFromCourse(studentId, courseId);
//        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto("Student removed from course!"));
//    }

    @PostMapping
    public ResponseEntity<CourseDto> add(@RequestBody @Valid AddCourseCommand addCourse) {
//        Course course = new Course(
//                addCourseCommand.getName(),
//                addCourseCommand.getAgeLimit()
//        );
        //Course course = mapper.map(addCourse, Course.class);
        Course course = courseManagementService.add(mapper.map(addCourse, Course.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.map(course, CourseDto.class));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> get(@PathVariable long id) {
        Course course = courseManagementService.get(id);
        return ResponseEntity.ok(mapper.map(course, CourseDto.class));
    }

    @GetMapping
    public ResponseEntity<Page<CourseDto>> getAll(@PageableDefault Pageable pageable,
                                                  @RequestParam(required = false, value = "search") String search) {
        if (search != null) {
            CourseSpecificationsBuilder builder = new CourseSpecificationsBuilder();
            String operationSetExper = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
            Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
            }
            Specification<Course> spec = builder.build();
            return ResponseEntity.ok(
                    courseManagementService.showAll(spec, pageable).map(c -> mapper.map(c, CourseDto.class))
            );
        }

        return ResponseEntity.ok(
                courseManagementService.showAll(pageable).map(c -> mapper.map(c, CourseDto.class))
        );
    }

//    @GetMapping
//    public ResponseEntity<Page<CourseDto>> getAll(@PageableDefault Pageable pageable,
//                                                  @RequestParam(required = false, value = "search") String search) {
//        Page<CourseDto> courses = courseManagementService
//                .showAll(pageable)
//                .map(c -> mapper.map(c, CourseDto.class));
//        return ResponseEntity.ok(courses);
//    }


    @PutMapping
    public ResponseEntity<CourseDto> edit(@RequestBody EditCourseCommand editCourseCommand) {
//        Course course = courseManagementService.get(editCourseCommand.getId());
//        course.setName(editCourseCommand.getName());
//        course.setAgeLimit(editCourseCommand.getAgeLimit());
//        course = courseManagementService.edit(course);
        Course course = courseManagementService.edit(editCourseCommand);
        return ResponseEntity.ok(mapper.map(course, CourseDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StatusDto> delete(@PathVariable("id") long id) {
        courseManagementService.delete(id);
        return ResponseEntity.ok(new StatusDto("Course removed!"));
    }

//    @GetMapping("/without-student")
//    public ResponseEntity<List<CourseDto>> getAllWithoutStudent(@PageableDefault Pageable pageable) {
//        List<CourseDto> courses = courseManagementService.showAll(pageable)
//                .stream()
//                .filter(c -> c.getStudents().size() == 0)
//                .map(c -> mapper.map(c, CourseDto.class))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(courses);
//    }
//
//    @GetMapping("/enrolled-student/{id}")
//    public ResponseEntity<List<CourseDto>> getAllForEnrolledStudent(@PathVariable("id") long id,
//                                                                    @PageableDefault Pageable pageable) {
//        List<CourseDto> courses = courseManagementService.showAll(pageable)
//                .stream()
//                .filter(c -> c.hasStudent(id))
//                .map(c -> mapper.map(c, CourseDto.class))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(courses);
//    }

    @PostMapping("/enroll")
    public ResponseEntity<StatusDto> enroll(@RequestBody @Valid EnrollStudentToCourseCommand enroll) {
        courseManagementService.addStudentToCourse(enroll);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StatusDto("Student enrolled to course!"));
    }

//    @PutMapping("/remove/student/{studentId}/course/{courseId}")
//    public ResponseEntity<StatusDto> cancel(@PathVariable("studentId") long studentId, @PathVariable("courseId") long courseId) {
//        courseManagementService.removeStudentFromCourse(studentId, courseId);
//        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto("Student removed from course!"));
//    }

    @PostMapping("/remove")
    public ResponseEntity<StatusDto> cancel(@RequestBody @Valid RemoveStudentFromCourseCommand remove) {
        courseManagementService.removeStudentFromCourse(remove.getStudentId(), remove.getCourseId());
        return ResponseEntity.status(HttpStatus.OK).body(new StatusDto("Student removed from course!"));
    }


}
