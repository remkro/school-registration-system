package pl.kurs.schoolregistrationsystem.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import pl.kurs.schoolregistrationsystem.model.dto.StudentWithCoursesDto;
import pl.kurs.schoolregistrationsystem.model.entity.Course;
import pl.kurs.schoolregistrationsystem.model.entity.Student;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentToStudentWithCoursesDto implements Converter<Student, StudentWithCoursesDto> {

    @Override
    public StudentWithCoursesDto convert(MappingContext<Student, StudentWithCoursesDto> mappingContext) {
        Student student = mappingContext.getSource();
        List<String> courses = student.getCourses().
                stream()
                .map(Course::getName)
                .collect(Collectors.toList());
        return new StudentWithCoursesDto(student.getId(), student.getFirstName(), student.getLastName(),
                student.getPesel(), student.getEmail(), courses);
    }

}
