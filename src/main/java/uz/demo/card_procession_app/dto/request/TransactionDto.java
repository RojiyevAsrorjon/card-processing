package uz.demo.card_procession_app.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionDto {
    @NotNull(message = "external_id")
    String externalId;
    @NotNull(message = "amount")
    Long amount;
    @Nullable
    String currency;
    @Nullable
    String purpose;
}
