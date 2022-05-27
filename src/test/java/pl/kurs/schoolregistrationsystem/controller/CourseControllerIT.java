package pl.kurs.schoolregistrationsystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import pl.kurs.schoolregistrationsystem.command.EditCourseCommand;
import pl.kurs.schoolregistrationsystem.command.EnrollStudentToCourseCommand;
import pl.kurs.schoolregistrationsystem.model.dto.CourseDto;
import pl.kurs.schoolregistrationsystem.model.dto.StatusDto;
import pl.kurs.schoolregistrationsystem.model.dto.StudentWithCoursesDto;
import pl.kurs.schoolregistrationsystem.repository.CourseRepository;
import pl.kurs.schoolregistrationsystem.repository.StudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SchoolRegistrationSystemApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CourseControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @AfterEach
    void cleanUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldAddSingleCourse() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);

        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(addCourseCommand.getName()))
                .andExpect(jsonPath("$.ageLimit").value(addCourseCommand.getAgeLimit()));
    }

    @Test
    void shouldGetSingleCourse() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(result.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        postman.perform(MockMvcRequestBuilders.get("/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.name").value(courseDto.getName()))
                .andExpect(jsonPath("$.ageLimit").value(courseDto.getAgeLimit()));
    }

    @Test
    void shouldGetAllCourses() throws Exception {
        AddCourseCommand addCourseCommand1 = new AddCourseCommand("testowy kurs 1", 1);
        String addCourseCommandJson1 = mapper.writeValueAsString(addCourseCommand1);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson1))
                .andDo(print());

        AddCourseCommand addCourseCommand2 = new AddCourseCommand("testowy kurs 2", 2);
        String addCourseCommandJson2 = mapper.writeValueAsString(addCourseCommand2);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson2))
                .andDo(print());

        AddCourseCommand addCourseCommand3 = new AddCourseCommand("testowy kurs 3", 3);
        String addCourseCommandJson3 = mapper.writeValueAsString(addCourseCommand3);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson3))
                .andDo(print());

        MvcResult getAllResult = postman.perform(MockMvcRequestBuilders.get("/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<CourseDto> courses = mapper.readValue(getAllResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(3, courses.size());
        assertEquals(addCourseCommand1.getName(), courses.get(0).getName());
        assertEquals(addCourseCommand1.getAgeLimit(), courses.get(0).getAgeLimit());
        assertEquals(addCourseCommand2.getName(), courses.get(1).getName());
        assertEquals(addCourseCommand2.getAgeLimit(), courses.get(1).getAgeLimit());
        assertEquals(addCourseCommand3.getName(), courses.get(2).getName());
        assertEquals(addCourseCommand3.getAgeLimit(), courses.get(2).getAgeLimit());
    }

    @Test
    void shouldGetAllCoursesWithPagination() throws Exception {
        AddCourseCommand addCourseCommand1 = new AddCourseCommand("testowy kurs 1", 1);
        String addCourseCommandJson1 = mapper.writeValueAsString(addCourseCommand1);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson1))
                .andDo(print());

        AddCourseCommand addCourseCommand2 = new AddCourseCommand("testowy kurs 2", 2);
        String addCourseCommandJson2 = mapper.writeValueAsString(addCourseCommand2);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson2))
                .andDo(print());

        AddCourseCommand addCourseCommand3 = new AddCourseCommand("testowy kurs 3", 3);
        String addCourseCommandJson3 = mapper.writeValueAsString(addCourseCommand3);
        postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson3))
                .andDo(print());

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/courses?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<CourseDto> courses = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(2, courses.size());
        assertEquals(addCourseCommand1.getName(), courses.get(0).getName());
        assertEquals(addCourseCommand1.getAgeLimit(), courses.get(0).getAgeLimit());
        assertEquals(addCourseCommand2.getName(), courses.get(1).getName());
        assertEquals(addCourseCommand2.getAgeLimit(), courses.get(1).getAgeLimit());
    }

    @Test
    void shouldEditSingleCourse() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(result.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        EditCourseCommand editCourseCommand = new EditCourseCommand(courseId, "zmieniona nazwa kursu", 18);
        String editCourseCommandJson = mapper.writeValueAsString(editCourseCommand);
        postman.perform(MockMvcRequestBuilders.put("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editCourseCommandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.name").value(editCourseCommand.getName()))
                .andExpect(jsonPath("$.ageLimit").value(editCourseCommand.getAgeLimit()));
    }

    @Test
    void shouldDeleteSingleCourse() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andReturn();
        CourseDto courseDto = mapper.readValue(result.getResponse().getContentAsString(), CourseDto.class);
        Long courseId = courseDto.getId();

        MvcResult deleteResult = postman.perform(MockMvcRequestBuilders.delete("/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andReturn();
        StatusDto statusDto = mapper.readValue(deleteResult.getResponse().getContentAsString(), StatusDto.class);

        MvcResult getAllResult = postman.perform(MockMvcRequestBuilders.get("/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        List<CourseDto> courses = mapper.readValue(getAllResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Course removed!", statusDto.getStatus());
        assertEquals(0, courses.size());
    }

    @Test
    void shouldFailingAddingSingleCourseWhenNameIsEmpty() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("", 1);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("COURSE_NAME_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailingAddingSingleCourseWhenAgeLimitIsEmpty() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", null);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("AGE_LIMIT_CANNOT_BE_NULL"));
    }

    @Test
    void shouldFailingAddingSingleCourseWhenAgeLimitIsNegative() throws Exception {
        AddCourseCommand addCourseCommand = new AddCourseCommand("testowy kurs", -10);
        String addCourseCommandJson = mapper.writeValueAsString(addCourseCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addCourseCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("AGE_LIMIT_MUST_BE_POSITIVE"));
    }

    @Test
    void shouldFailDeletingCourseWhenCourseHasStudents() throws Exception {
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
        MvcResult enrollStudentToCourseResult = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andReturn();

        MvcResult deleteResult = postman.perform(MockMvcRequestBuilders.delete("/courses/{id}", courseId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = deleteResult.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("COURSE_HAS_STUDENTS_THUS_CANNOT_BE_DELETED"));
    }

}