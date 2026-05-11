package cl.duoc.lmstockms.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    String title1 = "Error validación de datos."; //para HTTP 400
    String title2 = "Conflicto de datos."; //para HTTP 409
    String title3 = "Objeto no encontrado"; //para HTTP 404

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handlerValidation(MethodArgumentNotValidException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatus(400);
        problemDetail.setTitle(title1);

        var errors = exception.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (String existing, String replacement) -> existing));

        problemDetail.setProperty("Parámetro no válido", errors);
        return problemDetail;
    }

    @ExceptionHandler(IdExisteException.class)
    public ProblemDetail handlerNoExisteID(IdExisteException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatus(409); //HTTP 409 = conlficto de datos
        problemDetail.setTitle(title2);
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(IdNoExisteException.class)
    public ProblemDetail handlerNoExisteID(IdNoExisteException exception){
        ProblemDetail problemDetail = ProblemDetail.forStatus(404); //HTTP 404 = no encontrado
        problemDetail.setTitle(title3);
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

}
