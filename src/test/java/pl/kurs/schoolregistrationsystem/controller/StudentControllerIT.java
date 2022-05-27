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
import pl.kurs.schoolregistrationsystem.command.EditStudentCommand;
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
class StudentControllerIT {

    @Autowired
    private MockMvc postman;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @AfterEach
    void cleanUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
    }

    @Test
    void shouldAddSingleStudent() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);

        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value(addStudentCommand.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(addStudentCommand.getLastName()))
                .andExpect(jsonPath("$.pesel").value(addStudentCommand.getPesel()))
                .andExpect(jsonPath("$.email").value(addStudentCommand.getEmail()));
    }

    @Test
    void shouldGetSingleStudent() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(result.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        postman.perform(MockMvcRequestBuilders.get("/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.firstName").value(addStudentCommand.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(addStudentCommand.getLastName()))
                .andExpect(jsonPath("$.pesel").value(addStudentCommand.getPesel()))
                .andExpect(jsonPath("$.email").value(addStudentCommand.getEmail()));
    }

    @Test
    void shouldGetAllStudents() throws Exception {
        AddStudentCommand addStudentCommand1 = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson1 = mapper.writeValueAsString(addStudentCommand1);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson1))
                .andDo(print());

        AddStudentCommand addStudentCommand2 = new AddStudentCommand("Stefan", "Tomaszewski", "52053147885", "stefu@email.com");
        String addStudentCommandJson2 = mapper.writeValueAsString(addStudentCommand2);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson2))
                .andDo(print());

        AddStudentCommand addStudentCommand3 = new AddStudentCommand("Andrzej", "Marcinkowski", "63072716299", "marcinq@email.com");
        String addStudentCommandJson3 = mapper.writeValueAsString(addStudentCommand3);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson3))
                .andDo(print());

        MvcResult getAllResult = postman.perform(MockMvcRequestBuilders.get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<StudentWithCoursesDto> students = mapper.readValue(getAllResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(3, students.size());
        assertEquals(addStudentCommand1.getFirstName(), students.get(0).getFirstName());
        assertEquals(addStudentCommand1.getLastName(), students.get(0).getLastName());
        assertEquals(addStudentCommand1.getPesel(), students.get(0).getPesel());
        assertEquals(addStudentCommand1.getEmail(), students.get(0).getEmail());
        assertEquals(addStudentCommand2.getFirstName(), students.get(1).getFirstName());
        assertEquals(addStudentCommand2.getLastName(), students.get(1).getLastName());
        assertEquals(addStudentCommand2.getPesel(), students.get(1).getPesel());
        assertEquals(addStudentCommand2.getEmail(), students.get(1).getEmail());
        assertEquals(addStudentCommand3.getFirstName(), students.get(2).getFirstName());
        assertEquals(addStudentCommand3.getLastName(), students.get(2).getLastName());
        assertEquals(addStudentCommand3.getPesel(), students.get(2).getPesel());
        assertEquals(addStudentCommand3.getEmail(), students.get(2).getEmail());
    }

    @Test
    void shouldGetAllStudentsWithPagination() throws Exception {
        AddStudentCommand addStudentCommand1 = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson1 = mapper.writeValueAsString(addStudentCommand1);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson1))
                .andDo(print());

        AddStudentCommand addStudentCommand2 = new AddStudentCommand("Stefan", "Tomaszewski", "52053147885", "stefu@email.com");
        String addStudentCommandJson2 = mapper.writeValueAsString(addStudentCommand2);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson2))
                .andDo(print());

        AddStudentCommand addStudentCommand3 = new AddStudentCommand("Andrzej", "Marcinkowski", "63072716299", "marcinq@email.com");
        String addStudentCommandJson3 = mapper.writeValueAsString(addStudentCommand3);
        postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson3))
                .andDo(print());

        MvcResult result = postman.perform(MockMvcRequestBuilders.get("/students?page=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        List<StudentWithCoursesDto> students = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertEquals(2, students.size());
        assertEquals(addStudentCommand1.getFirstName(), students.get(0).getFirstName());
        assertEquals(addStudentCommand1.getLastName(), students.get(0).getLastName());
        assertEquals(addStudentCommand1.getPesel(), students.get(0).getPesel());
        assertEquals(addStudentCommand1.getEmail(), students.get(0).getEmail());
        assertEquals(addStudentCommand2.getFirstName(), students.get(1).getFirstName());
        assertEquals(addStudentCommand2.getLastName(), students.get(1).getLastName());
        assertEquals(addStudentCommand2.getPesel(), students.get(1).getPesel());
        assertEquals(addStudentCommand2.getEmail(), students.get(1).getEmail());
    }

    @Test
    void shouldEditSingleStudent() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(result.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        EditStudentCommand editStudentCommand = new EditStudentCommand(studentId, "Zenek", "Grzebielucha",
                "78052796912", "zmieniony@email.com");
        String editStudentCommandJson = mapper.writeValueAsString(editStudentCommand);
        postman.perform(MockMvcRequestBuilders.put("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editStudentCommandJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.firstName").value(editStudentCommand.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(editStudentCommand.getLastName()))
                .andExpect(jsonPath("$.pesel").value(editStudentCommand.getPesel()))
                .andExpect(jsonPath("$.email").value(editStudentCommand.getEmail()));
    }

    @Test
    void shouldDeleteSingleStudent() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andReturn();
        StudentWithCoursesDto studentWithCoursesDto = mapper.readValue(result.getResponse().getContentAsString(), StudentWithCoursesDto.class);
        Long studentId = studentWithCoursesDto.getId();

        MvcResult deleteResult = postman.perform(MockMvcRequestBuilders.delete("/students/{id}", studentId))
                .andExpect(status().isOk())
                .andReturn();
        StatusDto statusDto = mapper.readValue(deleteResult.getResponse().getContentAsString(), StatusDto.class);

        MvcResult getAllResult = postman.perform(MockMvcRequestBuilders.get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        List<StudentWithCoursesDto> courses = mapper.readValue(getAllResult.getResponse().getContentAsString(),
                new TypeReference<>() {
                });

        assertEquals("Student removed!", statusDto.getStatus());
        assertEquals(0, courses.size());
    }

    @Test
    void shouldFailingAddingSingleStudentWhenFirstNameIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("", "Jackowski", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("FIRST_NAME_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailingAddingSingleStudentWhenLastNameIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "", "18320263599", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("LAST_NAME_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailingAddingSingleStudentWhenPeselIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("PESEL_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailingAddingSingleStudentWhenPeselIsIncorrect() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "tomaszmaupapeel");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("EMAIL_INCORRECT"));
    }

    @Test
    void shouldFailingAddingSingleStudentWhenEmailIsEmpty() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "18320263599", "");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("EMAIL_CANNOT_BE_EMPTY"));
    }

    @Test
    void shouldFailingAddingSingleStudentWhenEmailIsIncorrect() throws Exception {
        AddStudentCommand addStudentCommand = new AddStudentCommand("Tomasz", "Jackowski", "183", "tomaszj@email.com");
        String addStudentCommandJson = mapper.writeValueAsString(addStudentCommand);
        MvcResult result = postman.perform(MockMvcRequestBuilders.post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addStudentCommandJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = result.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("PESEL_INCORRECT"));
    }

    @Test
    void shouldFailDeletingStudentWhenStudentHasCourses() throws Exception {
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
        MvcResult enrollStudenToCourseResult = postman.perform(MockMvcRequestBuilders.post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enrollStudentToCourseCommandJson))
                .andDo(print())
                .andReturn();

        MvcResult deleteResult = postman.perform(MockMvcRequestBuilders.delete("/students/{id}", studentId))
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseAsString = deleteResult.getResponse().getContentAsString();

        assertTrue(responseAsString.contains("STUDENT_HAS_COURSES_THUS_CANNOT_BE_DELETED"));
    }

}