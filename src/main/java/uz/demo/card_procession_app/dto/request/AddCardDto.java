package uz.demo.card_procession_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCardDto  implements Serializable {

    @NotNull(message = "Missing required field(s): user_id")
    private Long userId;
    @NotBlank(message = "Missing required field(s): status")
    private String status;
}
