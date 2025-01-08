package uz.demo.card_procession_app.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDto<T> implements Serializable {

    private T data;

    private AppErrorDto error;

    private boolean success;

    public DataDto(boolean success) {
        this.success = success;
    }

    public DataDto(T data) {
        this.data = data;
        this.success = true;
    }

    public DataDto(AppErrorDto error) {
        this.error = error;
        this.success = false;
    }

    public static <T> DataDto<T> of(T data) {
        return new DataDto<>(data);
    }

}
