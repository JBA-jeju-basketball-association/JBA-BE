package github.com.jbabe.web.advice;

import github.com.jbabe.service.exception.*;
import github.com.jbabe.web.advice.errorResponseDto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 요청 에러
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse errorResponse = new ErrorResponse(400, "BAD_REQUEST", ex.getDetailMessage(), ex.getRequest());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN) //권한이 없을때
    public ResponseEntity<ErrorResponse> handleNotAccessDenied(AccessDeniedException ex) {
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

    @ExceptionHandler(InvalidReqeustException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequestException(InvalidReqeustException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid_Request", ex.getDetailMessage(), ex.getRequest());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse methodValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("MethodArgumentNotValidException 발생!! url:{}, trace:{}", request.getRequestURL(), ex.getStackTrace());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid_Request", ex.getBindingResult().getFieldError().getDefaultMessage(), ex.getBindingResult().getFieldError().getField());
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ErrorResponse> methodValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
//        log.warn("MethodArgumentNotValidException 발생!! url:{}, trace:{}", request.getRequestURL(), ex.getStackTrace());
//         ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid_Request", ex.getBindingResult().getFieldError().getDefaultMessage(), ex.getBindingResult().getFieldError().getCode());
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse ExpiredJwtException(ExpiredJwtException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "ExpiredJwt", ex.getDetailMessage(), ex.getRequest());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse JwtException(JwtException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "JwtError", ex.getDetailMessage(), ex.getRequest());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse RedisConnectionFailureException(RedisConnectionFailureException ex) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RedisConnectionFail", ex.getDetailMessage(), ex.getRequest());
    }


}
