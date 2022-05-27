package pl.kurs.schoolregistrationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.schoolregistrationsystem.SchoolRegistrationSystemApplication;
import pl.kurs.schoolregistrationsystem.command.AddCourseCommand;
import pl.kurs.schoolregistrationsystem.command.AddStudentCommand;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.model.dto.CourseDto;
import pl.kurs.schoolregistrationsystem.model.dto.StudentWithCoursesDto;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SchoolRegistrationSystemApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class RegistrationControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Value("${student.max-courses}")
    private int maximumCourses;

    @Value("${course.max-students}")
    private int maximumStudents;

    @AfterEach
    void cleanUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldEnrollStudentToCourse() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, courseId);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("Student enrolled to course!"));
    }

    @Test
    public void shouldRemoveStudentFromCourse() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        MvcResult result = postman.perform(MockMvcRequestBuilders.put("/registration/remove/student/{studentId}/course/{courseId}", studentId, courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        MvcResult getStudentResult = postman.perform(MockMvcRequestBuilders.get("/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        studentWithCoursesDto = mapper.readValue(getStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);

        assertTrue(responseAsString.contains("Student removed from course!"));
        assertNull(studentWithCoursesDto.getCourses());
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenStudentIdIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(null, courseId);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("STUDENT_ID_CANNOT_BE_NULL"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenCourseIdIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, null);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("COURSE_ID_CANNOT_BE_NULL"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenStudentDoesntExist() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(100L, courseId);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("STUDENT_DOES_NOT_EXIST"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenCourseDoesntExist() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, 100L);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("COURSE_DOES_NOT_EXIST"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenStudentIsAlreadyEnrolledInThatCourse() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, courseId);
        String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
        MvcResult result1 = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andReturn();

        MvcResult result2 = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result2.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("STUDENT_ALREADY_ENROLLED_IN_THE_COURSE"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenStudentHasMaximumNumberOfCourses() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        String[] responses = new String[maximumCourses + 1];
        for (int i = 0; i < responses.length; i++) {
            AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs" + (i + 1), 1);
            String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
            MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(addCourseCommandJson))
                    .andDo(print())
                    .andReturn();
            CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
            Long courseId = courseDto.getId();

            EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, courseId);
            String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
            MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(enrollStudentToCourseCommandJson))
                    .andDo(print())
                    .andReturn();
            responses[i] = result.getResponse().getContentAsString();
        }
        String responseAsString = responses[maximumCourses];

        assertTrue(responseAsString.contains("STUDENT_IS_ENROLLED_TO_MAXIMUM_NUMBER_OF_COURSES"));
    }

    @Test
    void shouldFailEnrollingStudentToCourseWhenCourseHasMaximumNumberOfStudents() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult addCourseResult = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(addCourseResult.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        String[] responses = new String[maximumStudents + 1];
        for (int i = 0; i < responses.length; i++) {

            AddStudentCommand addStudentCommand = new AddStudentCommand("Student", "Przykladowy" + (i + 1),
                    "18320263599", "studencik@email.com");
            String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
            MvcResult addStudentResult = postman.perform(MockMvcRequestBuilders.post("/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(addStudentCommandJson))
                    .andDo(print())
                    .andReturn();
            StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(addStudentResult.getResponse().getContentAsString(), StudentWithCoursesDto.class);
            Long studentId = studentWithCoursesDto.getId();

            EnrollStudentToCourseCommand enrollStudentToCourseCommand = new EnrollStudentToCourseCommand(studentId, courseId);
            String enrollStudentToCourseCommandJson = mapper.writeValueAsString(enrollStudentToCourseCommand);
            MvcResult result = postman.perform(MockMvcRequestBuilders.post("/registration")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(enrollStudentToCourseCommandJson))
                    .andDo(print())
                    .andReturn();
            responses[i] = result.getResponse().getContentAsString();
        }
        String responseAsString = responses[maximumStudents];

        assertTrue(responseAsString.contains("COURSE_HAS_MAXIMUM_NUMBER_OF_STUDENTS"));
    }

}