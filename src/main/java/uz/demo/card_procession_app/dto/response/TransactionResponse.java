package uz.demo.card_procession_app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionResponse {

    UUID id;
    String externalId;
    UUID cardId;
    Long afterBalance;
    Long amount;
    String currency;
    String purpose;
    Long exchangeRate;
}
