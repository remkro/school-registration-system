package pl.kurs.schoolregistrationsystem.error.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.schoolregistrationsystem.error.exception.CourseHasStudentsException;
import pl.kurs.schoolregistrationsystem.error.exception.NoEntityException;
import pl.kurs.schoolregistrationsystem.error.exception.StudentHasCoursesException;
import pl.kurs.schoolregistrationsystem.error.exception.WrongIdException;
import pl.kurs.schoolregistrationsystem.error.exceptionhandling.dto.ExceptionResponseDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<List<ExceptionResponseDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ExceptionResponseDto> errors = e.getAllErrors()
                .stream()
                .map(ve -> new ExceptionResponseDto(ve.getDefaultMessage(), LocalDateTime.now(), "BAD_REQUEST"))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({WrongIdException.class})
    public ResponseEntity<ExceptionResponseDto> handleWrongIdException(Exception e) {
        ExceptionResponseDto response = new ExceptionResponseDto(e.getMessage(), LocalDateTime.now(), "BAD_REQUEST");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({NoEntityException.class})
    public ResponseEntity<ExceptionResponseDto> handleNoEntityException(Exception e) {
        ExceptionResponseDto response = new ExceptionResponseDto(e.getMessage(), LocalDateTime.now(), "BAD_REQUEST");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({CourseHasStudentsException.class})
    public ResponseEntity<ExceptionResponseDto> handleCourseHasStudentsException(Exception e) {
        ExceptionResponseDto response = new ExceptionResponseDto(e.getMessage(), LocalDateTime.now(), "BAD_REQUEST");
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({StudentHasCoursesException.class})
    public ResponseEntity<ExceptionResponseDto> handleStudentHasCoursesException(Exception e) {
        ExceptionResponseDto response = new ExceptionResponseDto(e.getMessage(), LocalDateTime.now(), "BAD_REQUEST");
        return ResponseEntity.badRequest().body(response);
    }

}
