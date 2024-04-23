package tech.maxjung.tech.maxjung.util.http;

import org.springframework.http.HttpStatus;
import java.time.ZonedDateTime;

public record HttpErrorInfo(ZonedDateTime timestamp, HttpStatus httpStatus, String path, String message) {

  public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
    this(ZonedDateTime.now(), httpStatus, path, message);
  }

  public int getStatus() {
    return httpStatus.value();
  }

  public String getError() {
    return httpStatus.getReasonPhrase();
  }
}
