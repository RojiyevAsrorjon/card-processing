package uz.demo.card_procession_app.entity.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;
    private String type;
    private String externalId;
    private UUID cardId;
    private Long amount;
    private String currency;
    private String purpose;
    private Long afterBalance;
    private Long exchangeRate;
    private Timestamp createdAt;
}
