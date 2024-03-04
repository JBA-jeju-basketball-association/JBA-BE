package github.com.jbabe.web.advice;

import github.com.jbabe.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 요청 에러
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(400, "BAD_REQUEST", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDenied.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) //권한이 없을때
    public ResponseEntity<ErrorResponse> handleNotAccessDenied(AccessDenied ex) {
        ErrorResponse errorRequestResponse = new ErrorResponse(403, "FORBIDDEN" ,  ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorRequestResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountLockedException.class)
    @ResponseStatus(HttpStatus.LOCKED) //계정이 잠겼을때
    public ResponseEntity<ErrorResponse> handleAccountLockedException(AccountLockedException ex) {
        ErrorResponse errorRequestResponse = new ErrorResponse(423, "LOCKED", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorRequestResponse, HttpStatus.LOCKED);
    }


    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT) //입력한 키가 이미 존재할때
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(ConflictException ex) {
        ErrorResponse errorRequestResponse = new ErrorResponse(409, "CONFLICT", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorRequestResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE) //처리할 수 없는 요청
    public ResponseEntity<ErrorResponse> handleNotAcceptException(NotAcceptableException ex) {
        ErrorResponse errorRequestResponse = new ErrorResponse(406, "NOT_ACCEPTABLE", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorRequestResponse, HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) //찾을 수 없을때
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        ErrorResponse errorRequestResponse = new ErrorResponse(404, "NOT_FOUND", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorRequestResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomBadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED) //비밀번호가 틀렸을때
    public ErrorResponse handleBadCredentialsException(CustomBadCredentialsException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.name(), ex.getDetailMessage(), ex.getRequest());
    }

}
