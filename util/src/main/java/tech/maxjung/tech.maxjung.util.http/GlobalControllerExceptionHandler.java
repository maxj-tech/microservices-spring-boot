package tech.maxjung.tech.maxjung.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tech.maxjung.api.exceptions.BadRequestException;
import tech.maxjung.api.exceptions.InvalidInputException;
import tech.maxjung.api.exceptions.NotFoundException;

import static org.springframework.http.HttpStatus.*;


/* instead of having repetitive try-catch blocks in each REST controller,
  @RestControllerAdvice lets you define error handling logic in one place for all your controllers.
 */
@RestControllerAdvice
class GlobalControllerExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  public @ResponseBody HttpErrorInfo handleBadRequestExceptions(
      ServerHttpRequest request, BadRequestException ex) {
    return createHttpErrorInfo(BAD_REQUEST, request, ex);
  }

  // this maps a InvalidInputException to a HTTP 422 Unprocessable Entity status code
  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException(
    ServerHttpRequest request, InvalidInputException ex) {

    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
          ServerHttpRequest request, NotFoundException ex) {

    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(
    HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {

    final String path = request.getPath().pathWithinApplication().value();
    final String message = ex.getMessage();

    LOG.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
    return new HttpErrorInfo(httpStatus, path, message);
  }
}
