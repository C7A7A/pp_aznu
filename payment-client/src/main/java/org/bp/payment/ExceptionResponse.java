package org.bp.payment;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ExceptionResponse {
  private Date timestamp;
  private String message;
  private String details;

  @JsonIgnoreProperties(ignoreUnknown = true)
  public ExceptionResponse(Date timestamp, String message, String details) {
    super();
    this.timestamp = timestamp;
    this.message = message;
    this.details = details;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

  public String getDetails() {
    return details;
  }

}