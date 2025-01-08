package uz.demo.card_procession_app.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppErrorDto implements Serializable {

    private Timestamp timestamp;
    private Integer code;
    private String message;
    private String path;
    private String error;

    @Builder
    public AppErrorDto(HttpStatus code, String message, WebRequest request) {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.code = code.value();
        this.error = code.getReasonPhrase();
        this.message = message;
        this.path = ((ServletWebRequest) request).getRequest().getRequestURI();
    }


    public AppErrorDto(HttpStatus code, String message) {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.code = code.value();
        this.error = code.getReasonPhrase();
        this.message = message;
    }

    public AppErrorDto(Integer code, String message) {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now());
        this.code = code;
        this.error = message;
        this.message = message;
    }
}